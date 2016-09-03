![imgix logo](https://assets.imgix.net/imgix-logo-web-2014.pdf?page=2&fm=png&w=200&h=200)

[![Build Status](https://travis-ci.org/imgix/imgix-java.png?branch=master)](https://travis-ci.org/imgix/imgix-java)

A Java client library for generating URLs with imgix. imgix is a high-performance
distributed image processing service. More information can be found at
[http://www.imgix.com](http://www.imgix.com).


Dependencies
------------

The library itself has no external dependencies. Although if you want to build from source (or run tests) then you need `ant` and the `JDK 1.6+`.

Creating a jar
--------------

To create a jar from source:

```
gradle build
```

This creates `imgix-java-{VERSION_NUMBER}.jar` under `./build/libs`

Once a new version has been merged into master on GitHub (don't forget to update the version numbers in build.gradle first!), it can be deployed to Bintray with `gradle build && gradle bintrayUpload`. After that, the new version can be viewed via the [Bintray web interface](https://bintray.com/imgix/imgix-java/imgix-java).

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


Domain Sharded URLs
-------------------

Domain sharding enables you to spread image requests across multiple domains.
This allows you to bypass the requests-per-host limits of browsers. We
recommend 2-3 domain shards maximum if you are going to use domain sharding.

In order to use domain sharding, you need to add multiple domains to your
source. You then provide an array of these domains to a builder.

```java
import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class ImgixExample {
    public static void main(String[] args) {
        String[] domains = new String[] { "demos-1.imgix.net", "demos-2.imgix.net", "demos-3.imgix.net"};
        URLBuilder builder = new URLBuilder(domains);
        Map<String, String> params = new HashMap<String, String>();
        params.put("w", "100");
        params.put("h", "100");
        System.out.println(builder.createURL("bridge.png", params));
        System.out.println(builder.createURL("flower.png", params));
    }
}

// Prints out:
// http://demos-1.imgix.net/bridge.png?h=100&w=100
// http://demos-2.imgix.net/flower.png?h=100&w=100
```

By default, shards are calculated using a checksum so that the image path
always resolves to the same domain. This improves caching in the browser.
However, you can supply a different strategy that cycles through domains
instead. For example:

```java
import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class ImgixExample {
    public static void main(String[] args) {
        String[] domains = new String[] { "demos-1.imgix.net", "demos-2.imgix.net", "demos-3.imgix.net"};
        URLBuilder builder = new URLBuilder(domains);
        builder.setShardStratgy(URLBuilder.ShardStrategy.CYCLE); // set shard strategy
        Map<String, String> params = new HashMap<String, String>();
        params.put("w", "100");
        params.put("h", "100");
        System.out.println(builder.createURL("bridge.png", params));
        System.out.println(builder.createURL("bridge.png", params));
        System.out.println(builder.createURL("bridge.png", params));
        System.out.println(builder.createURL("bridge.png", params));
    }
}

// Prints out:
// http://demos-1.imgix.net/bridge.png?h=100&w=100
// http://demos-2.imgix.net/bridge.png?h=100&w=100
// http://demos-3.imgix.net/bridge.png?h=100&w=100
// http://demos-1.imgix.net/bridge.png?h=100&w=100
```
