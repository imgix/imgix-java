<!-- ix-docs-ignore -->
![imgix logo](https://assets.imgix.net/sdk-imgix-logo.svg)

`imgix-java` is a client library for generating image URLs with [imgix](https://www.imgix.com/).

[![Download](https://api.bintray.com/packages/imgix/maven/imgix-java/images/download.svg) ](https://bintray.com/imgix/maven/imgix-java/_latestVersion)
[![Build Status](https://travis-ci.org/imgix/imgix-java.svg?branch=master)](https://travis-ci.org/imgix/imgix-java)
[![License](https://img.shields.io/github/license/imgix/imgix-java)](https://github.com/imgix/imgix-java/blob/master/LICENSE)

---
<!-- /ix-docs-ignore -->

- [Dependencies](#dependencies)
- [Install Options](#install-options)
  - [Gradle & JCenter](#gradle--jcenter)
  - [Creating a Jar](#creating-a-jar)
- [Basic Usage](#basic-usage)
- [Signed URLs](#signed-urls)
- [Srcset Generation](#srcset-generation)
- [Running Tests](#running-tests)

Dependencies
------------

The library itself has no external dependencies. Although if you want to build from source (or run tests) then you need `ant` and the `JDK 1.6+`.

## Install Options

### Gradle & JCenter

To add Imgix-Java to your project, include the following in your project's build.gradle:

```
dependencies {
   compile "com.imgix:imgix-java:2.2.0"
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

Basic Usage
-----------

To begin creating imgix URLs, add the jar to your project's classpath and import the imgix library. The URL builder can be reused to create URLs for any images on the domains it is provided.

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


HTTPS support is available by default. However, if you need HTTP support, call setUseHttps on the builder:

```java
import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class ImgixExample {
    public static void main(String[] args) {
        URLBuilder builder = new URLBuilder("demos.imgix.net");

        builder.setUseHttps(false); // use http

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


Srcset Generation
-----------

The imgix-java library allows for generation of custom `srcset` attributes, which can be invoked through `createSrcSet()`. By default, the `srcset` generated will allow for responsive size switching by building a list of image-width mappings.

```java
URLBuilder ub = new URLBuilder("demos.imgix.net", true, "my-token", false);
String srcset = ub.createSrcSet("bridge.png");
System.out.println(srcset);
```

Will produce the following attribute value, which can then be served to the client:

```html
https://demos.imgix.net/bridge.png?w=100&s=494158d968e94ac8e83772ada9a83ad1 100w,
https://demos.imgix.net/bridge.png?w=116&s=6a22236e189b6a9548b531330647ffa7 116w,
https://demos.imgix.net/bridge.png?w=134&s=cbf91f556dd67c0b9e26cb9784a83794 134w,
                                    ...
https://demos.imgix.net/bridge.png?w=7400&s=503e3ba04588f1c301863c9a5d84fe91 7400w,
https://demos.imgix.net/bridge.png?w=8192&s=152551ce4ec155f7a03f60f762a1ca33 8192w
```

In cases where enough information is provided about an image's dimensions, `createSrcSet()` will instead build a `srcset` that will allow for an image to be served at different resolutions. The parameters taken into consideration when determining if an image is fixed-width are `w` (width), `h` (height), and `ar` (aspect ratio). By invoking `createSrcSet()` with either a width **or** the height and aspect ratio (along with `fit=crop`, typically) provided, a different `srcset` will be generated for a fixed-size image instead.

```java
URLBuilder ub = new URLBuilder("demos.imgix.net", true, "my-token", false);
HashMap<String,String> params = new HashMap<String,String> ();
params.put("h", "200");
params.put("ar", "3:2");
params.put("fit", "crop");
String srcset = ub.createSrcSet("bridge.png", params);
System.out.println(srcset);

```

Will produce the following attribute value:

```html
https://demos.imgix.net/bridge.png?ar=3%3A2&dpr=1&fit=crop&h=200&s=4c79373f535df7e2594a8f6622ec6631 1x,
https://demos.imgix.net/bridge.png?ar=3%3A2&dpr=2&fit=crop&h=200&s=dc818ae4522494f2f750651304a4d825 2x,
https://demos.imgix.net/bridge.png?ar=3%3A2&dpr=3&fit=crop&h=200&s=ba1ec0cef6c77ff02330d40cc4dae932 3x,
https://demos.imgix.net/bridge.png?ar=3%3A2&dpr=4&fit=crop&h=200&s=b51e497d9461be62354c0ea12b6524fb 4x,
https://demos.imgix.net/bridge.png?ar=3%3A2&dpr=5&fit=crop&h=200&s=dc37c1fbee505d425ca8e3764b37f791 5x
```

For more information to better understand `srcset`, we recommend [Eric Portis' "Srcset and sizes" article](https://ericportis.com/posts/2014/srcset-sizes/) which goes into depth about the subject.

Running Tests
-------------

To run tests clone this project and run:

```
gradle test
```

Dependencies for running tests (junit, etc) are provided (in `test/lib` and referenced in the build config).
