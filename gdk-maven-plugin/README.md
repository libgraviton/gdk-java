# GDK Maven Plugin

## What is it
The GDK Maven Plugin provides the possibility to generate Graviton POJOs during `mvn install`.

## Using the plugin
You can use this library in your project by including this in your `pom.xml`:

```xml
<build>
	<plugins>
		<plugin>
			<groupId>com.github.libgraviton</groupId>
			<artifactId>gdk-maven-plugin</artifactId>
			<version>LATEST</version>
			<configuration>
				<gravitonUrl>https://graviton.example.org</gravitonUrl>
				<generatorConfig>
				    <includeHashcodeAndEquals>false</includeHashcodeAndEquals>
					<useContextualClassNames>true</useContextualClassNames>
					<outputDirectory>the/output/dir</outputDirectory>
					<targetPackage>the.target.package</targetPackage>
				</generatorConfig>
			</configuration>
			<executions>
				<execution>
					<id>generate-pojos</id>
					<goals>
						<goal>generate</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

Make sure that `version` points to the latest GDK version on maven central.
IMPORTANT: To have working PATCH requests with GDK, `includeHashcodeAndEquals` and `includeToString` within generatorConfig needs to be configured false!

| config element         | description                                                                                                                                                                                                                                                           |
|:-----------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `gravitonUrl`          | The base url of the Graviton instance.                                                                                                                                                                                                                                |
| `endpointBlacklistPath`| Path to a file that contains a blacklist of all the endpoints to ignore (each line of the file represents one endpoint)                                                                                                                                                                                                                              |
| `endpointWhitelistPath`| Path to a file that contains a whitelist of all the endpoints to include (each line of the file represents one endpoint)                                                                                                                                                                                                                       |
| `generatorConfig`      | Configuration for the underlying `joelittlejohn/jsonschema2pojo` generator. For further config options see the [maven plugin documentation of the jsonschema2pojo generator](https://github.com/joelittlejohn/jsonschema2pojo/wiki/Getting-Started#the-maven-plugin). |
