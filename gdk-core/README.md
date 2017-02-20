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
GravitonApi gravitonApi = new GravitonApi();
try {
  GravitonResponse response1 = gravitonApi.get("123", Customer.class).execute();
  Customer customer1 = response1.getBodyItem(Customer.class);
} catch (CommunicationException e) {
  // Unable to obtain customer1
}

// as an alternative we could go for
Customer customer2 = new Customer();
customer2.setId("123");
try {
  GravitonResponse response2 = gravitonApi.get(customer).execute();
  customer2 = response2.getBodyItem(Customer.class);
} catch (CommunicationException e) {
  // Unable to obtain customer2
}

// or even
try {
  GravitonResponse response3 = gravitonApi.get("https://graviton-base-url/person/customer/123").execute();
  Customer customer3 = response3.getBodyItem(Customer.class);
} catch (CommunicationException e) {
  // Unable to obtain customer3
}
```

From this point on, all the REST calls are really simple to handle

```java
GravitonApi gravitonApi = new GravitonApi();

try {
  // POST request
  Customer customer = new Customer();
  customer.setFirstName("John");
  customer.setLastName("Smith");

  GravitonResponse response = gravitonApi.post(customer).execute();
  // What is the link to the newly created customer?
  String targetLink = response.getHeaders().getLink(LinkHeader.SELF);

  // GET request
  response = gravitonApi.get(targetLink).execute();
  Customer existingCustomer = response.getBodyItem(Customer.class);

  // PATCH request
  existingCustomer.setLastName("Fletcher");
  gravitonApi.patch(existingCustomer).execute();

  // DELETE request
  gravitonApi.delete(existingCustomer).execute();
} catch (CommunicationException e) {
  // Unable to complete example
}


```

### Special use case - file service

The file service endpoint behaves a little bit different from the other endpoints, since it allows us to retrieve the file itself or the file metadata via the same endpoint. Therefore the file service handling receives its own little helper, the **GravitonFile** class. In this case the **File** class is part of the generated POJOs

```java
GravitonFileEndpoint gravitonFileEndpoint = new GravitonFileEndpoint();

try {
  // GET the metadata
  File fileResource = new File();
  fileResource.setId("987");
  GravitonResponse response  = gravitonFileEndpoint.getMetadata(fileResource).execute();
  File metadata = response.getBody(File.class);

  // modify the metadata
  metadata.getMetadata().setFilename(System.currentTimeMillis() + ".txt");
  gravitonFileEndpoint.patch(metadata).execute();

  // GET the file itself
  response  = gravitonFileEndpoint.getFile(fileResource).execute();
  String data = response.getBodyItem();

  // create a new file via POST
  response = gravitonFileEndpoint.post(data, metadata).execute();
  String targetLink = response.getHeaders().getLink(LinkHeader.SELF);

  // DELETE the newly created file
  response  = gravitonFileEndpoint.getMetadata(targetLink).execute();
  metadata = response.getBodyItem(File.class);
  gravitonFileEndpoint.delete(metadata).execute();
} catch (CommunicationException e) {
  // Unable to complete example
}
```
