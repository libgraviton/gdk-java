# Graviton Development Kit for Java

[![Build Status](https://travis-ci.org/libgraviton/gdk-java.svg?branch=develop)](https://travis-ci.org/libgraviton/gdk-java) [![Coverage Status](https://coveralls.io/repos/github/libgraviton/gdk-java/badge.svg?branch=develop)](https://coveralls.io/github/libgraviton/gdk-java?branch=develop) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.libgraviton/gdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.libgraviton/gdk) [![javadoc.io](https://javadocio-badges.herokuapp.com/com.github.libgraviton/gdk-core/badge.svg)](https://javadocio-badges.herokuapp.com/com.github.libgraviton/gdk-core)

## What is it

The GDK is a base library which can be used for easily accessing a [Graviton](https://github.com/libgraviton/graviton) based REST API.

## Modules
The GDK for Java is split into two modules:
* **gdk-core**: Contains all the magic to use the generated POJOs with the REST API. See also the [gdk-core documentation](gdk-core/README.md).
* **gdk-maven-plugin**: Provides a maven plugin which allows you to generate Graviton POJOs during `mvn install`. See also the [maven plugin documentation](gdk-maven-plugin/README.md).

## API Doc

Please see the [gdk-core javadoc](http://www.javadoc.io/doc/com.github.libgraviton/gdk-core)

## Using the library

You can use this library in your project by including this in your `pom.xml`:

```xml
<dependencies>
	<dependency>
		<groupId>com.github.libgraviton</groupId>
		<artifactId>gdk-core</artifactId>
		<version>LATEST</version>
	</dependency>
</dependencies>
```

Make sure that `version` points to the newest release on maven central (see badge above).
