NanoHTTP
========

NanoHTTP is a dead simple, easy-to-use, lightweight HTTP Client for Java 8.

Do yourself a favor and start making HTTP calls like this:

```java
final URL resource = ...
final String text = HttpClient.defaultClient().get(resource).send().getBody().asString();
```
