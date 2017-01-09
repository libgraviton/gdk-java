# GDK Core

## What is it
The GDK Core provides the possibility to communicate with Graviton in an easy way.

## Configuration
All default values are configured within the **resources/default-gdk.properties** file (see the file itself for a description of each configuration).
Configurations can be overwritten, by just adding the specific entries to the **resource/app.properties** file in your project.

## How to use

First, make sure the POJOs are generated as described in the README.md from the **gdk-maven-plugin**.

The only must, when it comes to configuration overwrites, is the **graviton.base.url** property. Simply enter the Graviton base url and we are ready to go.

Let's say we want to execute a GET request and have an endpoint /person/customer where the gdk-maven-plugin created a matching Customer class.

```java
Graviton graviton = new Graviton();
GravitonResponse response1 = graviton.get("123", Customer.class).execute();
Customer customer1 = response1.getBody(Customer.class);

// as an alternative we could go for
Customer customer2 = new Customer();
customer2.setId("123");
GravitonResponse response2 = graviton.get(customer).execute();
customer2 = response2.getBody(Customer.class);

// or even
GravitonResponse response3 = graviton.get("https://graviton-base-url/person/customer/123").execute();
Customer customer3 = response3.getBody(Customer.class);
```

From this point on, all the REST calls are really simple to handle

```java
Graviton graviton = new Graviton();

// POST request
Customer customer = new Customer();
customer.setFirstName("John");
customer.setLastName("Smith");
GravitonResponse response = graviton.post(customer).execute();
// What is the link to the newly created customer?
String targetLink = response.getHeaders().getLink(LinkHeader.SELF);

// GET request
response = graviton.get(targetLink).execute();
Customer existingCustomer = response.getBody(Customer.class);

// PATCH request
existingCustomer.setLastName("Fletcher");
graviton.patch(existingCustomer).execute();

// DELETE request
graviton.delete(existingCustomer).execute();

```

### Special use case - file service

The file service endpoint behaves a little bit different from the other endpoints, since it allows us to retrieve the file itself or the file metadata via the same endpoint. Therefore the file service handling receives its own little helper, the **GravitonFile** class.

```java
GravitonFile gravitonFile = new GravitonFile();

// GET the metadata
File fileResource = new File();
fileResource.setId("987");
GravitonResponse response  = gravitonFile.getMetadata(fileResource).execute();
File metadata = response.getBody(File.class);

// modify the metadata
metadata.getMetadata().setFilename(System.currentTimeMillis() + ".txt");
gravitonFile.patch(metadata).execute();

// GET the file itself
response  = gravitonFile.getFile(fileResource).execute();
String data = response.getBody();

// create a new file via POST
response = gravitonFile.post(data, metadata).execute();
String targetLink = response.getHeaders().getLink(LinkHeader.SELF);

// DELETE the newly created file
response  = gravitonFile.getMetadata(targetLink).execute();
metadata = response.getBody(File.class);
gravitonFile.delete(metadata).execute();
```
