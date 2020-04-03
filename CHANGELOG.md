Change Log
==========

### Version 2.2.0 (_April 02, 2020_)

* feat(https): use https by default ([#38](https://github.com/imgix/imgix-java/pull/38))
* `build.gradle` updated in preparation for Gradle 7.0 (currently 6.3)

### Version 2.1.1 (_December 05, 2019_)

* fix: explicitly convert string to UTF-8 byte array ([#36](https://github.com/imgix/imgix-java/pull/36))

### Version 2.1.0 (_September 19, 2019_)

* fix: add domain validation during URLBuilder initialization ([#33](https://github.com/imgix/imgix-java/pull/33))
* feat: add srcset generation method ([#32](https://github.com/imgix/imgix-java/pull/32)) 

### Version 2.0.0 (_May 17, 2019_)

* fix: remove deprecated domain sharding functionality  ([#31](https://github.com/imgix/imgix-java/pull/31))
* refactor: rename `signWithLibraryParameter` to `includeLibraryParam` ([#30](https://github.com/imgix/imgix-java/pull/30))

### Version 1.2.0 (_May 17, 2019_)

* fix: deprecate domain sharding ([#29](https://github.com/imgix/imgix-java/pull/29))

### Version 1.1.12 (_Jul 19, 2018_)

* Support Java 10

### Version 1.1.10 (_Jun 15, 2017_)

* Adding javadoc and source jars to the build artifacts.

### Version 1.1.9 (_Jun 15, 2017_)

* No longer URL encode path components, which prevented signed URLs from working (and was also just wrong).

### Version 1.1.8 (_Sep 13, 2016_)

* Finally released to JCenter! https://bintray.com/imgix/maven/imgix-java

### Version 1.1.7 (_Jul 22, 2016_)

### Version 1.1.5 (_Jul 22, 2016_)

### Version 1.1.4 (_Jul 22, 2016_)

### Version 1.1.2 (_Jul 15, 2016_)

### Version 1.1.0 (_Feb 25, 2016_)

* Added automatic Base64 encoding for all Base64 variant parameters.
* Properly encoding all query string keys and values.
