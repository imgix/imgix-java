![imgix logo](https://assets.imgix.net/imgix-logo-web-2014.pdf?page=2&fm=png&w=200&h=200)

[![Build Status](https://travis-ci.org/imgix/imgix-java.png?branch=master)](https://travis-ci.org/imgix/imgix-java)
[ ![Download](https://api.bintray.com/packages/imgix/maven/imgix-java/images/download.svg?version=1.2.0) ](https://bintray.com/imgix/maven/imgix-java/1.2.0/link)

A Java client library for generating URLs with imgix. imgix is a high-performance
distributed image processing service. More information can be found at
[http://www.imgix.com](http://www.imgix.com).


Dependencies
------------

The library itself has no external dependencies. Although if you want to build from source (or run tests) then you need `ant` and the `JDK 1.6+`.

## Install Options

### Gradle & JCenter

To add Imgix-Java to your project, include the following in your project's build.gradle:

```
dependencies {
   compile "com.imgix:imgix-java:1.2.0"
}
```

And if this is your first external JCenter dependency you'll need to add, again to your project level build.gradle, the following:

```
buildscript {
   repositories {
      jcenter()
   }
}
```

### Creating a Jar

To create a jar from source:

```
gradle build
```

This creates `imgix-java-{VERSION_NUMBER}.jar` under `./build/libs`

Once a new version has been merged into master on GitHub (don't forget to update the version numbers in build.gradle first!), it can be deployed to Bintray with `gradle build && gradle bintrayUpload`. After that, the new version can be viewed via the [Bintray web interface](https://bintray.com/imgix/maven/imgix-java).

Running Tests
-------------

To run tests clone this project and run:

```
gradle test
```

Dependencies for running tests (junit, etc) are provided (in `test/lib` and referenced in the build config).

Basic Usage
-----------

To begin creating imgix URLs programmatically, simply add the jar to your project's classpath and import the imgix library. The URL builder can be reused to create URLs for any
images on the domains it is provided.

```java
import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class ImgixExample {
    public static void main(String[] args) {
        URLBuilder builder = new URLBuilder("demos.imgix.net");
        Map<String, String> params = new HashMap<String, String>();
        params.put("w", "100");
        params.put("h", "100");
        System.out.println(builder.createURL("bridge.png", params));
    }
}

// Prints out:
// http://demos.imgix.net/bridge.png?h=100&w=100
```


For HTTPS support, simply use the setter `setUseHttps` on the builder

```java
import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class ImgixExample {
    public static void main(String[] args) {
        URLBuilder builder = new URLBuilder("demos.imgix.net");
        builder.setUseHttps(true); // use https
        Map<String, String> params = new HashMap<String, String>();
        params.put("w", "100");
        params.put("h", "100");
        System.out.println(builder.createURL("bridge.png", params));
    }
}

// Prints out
// https://demos.imgix.net/bridge.png?h=100&w=100
```


Signed URLs
-----------

To produce a signed URL, you must enable secure URLs on your source and then
provide your signature key to the URL builder.

```java
import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class ImgixExample {
    public static void main(String[] args) {
        URLBuilder builder = new URLBuilder("demos.imgix.net");
        builder.setSignKey("test1234"); // set sign key
        Map<String, String> params = new HashMap<String, String>();
        params.put("w", "100");
        params.put("h", "100");
        System.out.println(builder.createURL("bridge.png", params));
    }
}

// Prints out:
// http://demos.imgix.net/bridge.png?h=100&w=100&s=bb8f3a2ab832e35997456823272103a4
```
