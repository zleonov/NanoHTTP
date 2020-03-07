NanoHTTP
========

NanoHTTP is a dead simple, fluent, easy-to-use, lightweight HTTP Client for Java 8.

Overview
========

Traditionally, if you wanted to do an HTTP call in Java, you had to use the [HttpURLConnection](https://docs.oracle.com/javase/8/docs/api/java/net/HttpURLConnection.html) class in the *java.net* package. Unfortunately *HttpURLConnection* is a low-level API that is unintuitive at best and downright awful at worst. The resulting code is cumbersome and hard to understand. If you’re wondering what a nightmare the API is, see this StackOverflow [post](https://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests/2793153#2793153).

NanoHTTP is a paper-thin wrapper around *HttpURLConnection* with a user friendly API. Do yourself a favor and start making HTTP calls like this:

```java
// A simple GET request
final URL resource = ...
try (final HttpResponse response = HttpClient.defaultClient().get(resource).send()){
   final String text = response.getBody().asString();
   ...
}
```

Goals
=====

- Easy to use for common cases
- A simple, concise, and user-friendly API which caters to 90 percent of application needs
- Java 8 or higher (Java 8 is still the most used version of the JDK)
- **No dependencies** (other than the JDK)
- Support for "Basic" HTTP authentication scheme
- Transparent GZip/Deflate support for HTTP responses
- GZip encoding for HTTP *PUT* and *POST* requests
- Support `application/x-www-form-urlencoded` content
- Support for `multipart/form-data` content
- Intuitive error handling
- And more...

Non-goals
=========
- Asynchronous HTTP requests
- Support for pluggable low-level HTTP transport libraries
- Object-Mapped (JSON/XML) responses
- HTTP/2 support

Warnings
========
At this point the API is not stable and subject to change.

Why another HTTP Client?
========================
It's true that there are more than several HTTP Client libraries flowing around the Java ecosystem. But there are several features that make NanoHTTP unique.

Since NanoHTTP closely matches the behavior of *HttpURLConnection*, it is ideal for users who are aware of or written code with *HttpURLConnection* idiosyncrasies in mind. And did I mention **zero dependencies**?

But if you want something more?
===============================
Here are some popular production-grade HTTP Clients:
- [JDK HttpClient](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html): A modern HTTP Client that supports both HTTP/1.1 and HTTP/2, added in Java 11 (as incubator module in Java 9) 
- [OkHttp](https://square.github.io/okhttp/): An HTTP Client for Android, Kotlin, and Java
- [google-http-java-Client](https://googleapis.github.io/google-http-java-client/): Google HTTP Client Library for Java and Android
- [Unirest for Java](http://kong.github.io/unirest-java/): A simplified, lightweight HTTP Client library
- [Jetty HTTP Client](https://www.eclipse.org/jetty/documentation/current/http-client-api.html): An efficient HTTP Client with a simple API that supports both asynchronous,  non-blocking, as well as synchronous, and blocking requests.

What's next?
============
- Publish the project on Maven Central
- Cookie handling
- This project is **sorely lacking** tests
    - Add more unit-tests
    - Add more integration-tests (we will need to decide how to mock HTTP responses, for example we may use [WireMock](https://github.com/tomakehurst/wiremock)
- Create a Wiki with frequently asked questions, examples, and guides
- Create a project site on GitHub pages and publish javadocs or link to [javadoc.io](https://www.javadoc.io/)

Can I help?
===========
Yes! Send me your patches and contributions. I will come up with a Contributor License Agreement. 