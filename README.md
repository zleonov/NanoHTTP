NanoHTTP
========

NanoHTTP is a dead simple, fluent, easy-to-use, lightweight HTTP Client for Java 8.

Overview
==========

Traditionally, if you wanted to do an HTTP call in Java, you had to use the [HttpURLConnection](https://docs.oracle.com/javase/8/docs/api/java/net/HttpURLConnection.html) class in the *java.net* package. Unfortunately *HttpURLConnection* is a low-level API that is unintuitive at best and downright awful at worst. The resulting code is cumbersome and hard to understand. If you’re wondering what a nightmare the API is, see this StackOverflow [post](https://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests/2793153#2793153).

NanoHTTP is a paper-thin wrapper around *HttpURLConnection* with a user friendly API. Do yourself a favor and start making HTTP calls like this:

```java
// A simple GET request
final String address = ...
try (final HttpResponse response = HttpClient.defaultClient().get(new URL(address)).send()){
   System.out.println(response.getBody().asString());
}
```
```java
// Send HTTP form content
final String address = ...
try (final HttpResponse response = HttpClient.defaultClient()
                                             .post(new URL(address))
                                             .setContentType("application/x-www-form-urlencoded")
                                             .setBody(new FormBuilder().encode("param1", "value1")
                                                                       .encode("param2", "value2")
                                                                       .build())
                                             .send())
{
   System.out.println(response.getStatusLine());
}
```
```java
// Send an audio FLAC file using GZip compression
final String address = ...
final Path song = ...
try (final HttpResponse response = HttpClient.defaultClient()
                                             .post(new URL(address))
                                             .setContentType("audio/x-flac; rate=16000")
                                             .setContentEncoding("gzip")
                                             .setBody(GZipEncoding.stream(() -> Files.newInputStream(song)))
                                             .send())
{
   System.out.println(response.getStatusLine());
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
- GZip support for HTTP *PUT* and *POST* requests
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

First, it doesn't just claim to be lightweight, it *IS* lightweight! As mentioned in the goals, it has **zero dependencies**.  
Second, it matches the behavior of *HttpURLConnection*, which makes it ideal to upgrade code written with *HttpURLConnection* idiosyncrasies in mind.  
Third, did we mention **zero dependencies**?

But if you want something more?
===============================
Here are some production grade HTTP Clients that I recommend:
- [JDK HttpClient](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html): A modern HTTP Client that supports both HTTP/1.1 and HTTP/2, added in Java 11 (incubator module in Java 9) 
- [OkHttp](https://square.github.io/okhttp/): An HTTP Client for Android, Kotlin, and Java
- [google-http-java-Client](https://googleapis.github.io/google-http-java-client/): Google HTTP Client Library for Java and Android
- [Unirest for Java](http://kong.github.io/unirest-java/): A Simplified, lightweight HTTP Client library
- [Apache HttpComponents](https://hc.apache.org/index.html): Java components focused on HTTP and associated protocols including HttpClient and HttpAsyncClient
- [Jetty HTTP Client](https://www.eclipse.org/jetty/documentation/current/http-client-api.html): An efficient HTTP Client with a simple API that supports both asynchronous,  non-blocking, as well as synchronous, and blocking requests.

What's next?
============
- Publish the project on Maven Central
- Add native support for HTTP <i>multipart/form-data</i> requests
- This project is **sorely lacking** tests
    - Add more unit-tests
    - Add more integration-tests (we will need to decide how to mock HTTP responses, for example we may use [WireMock](https://github.com/tomakehurst/wiremock)
- Create a Wiki with frequently asked questions, examples, and guides
- Create a project site on GitHub pages and publish javadocs or link to [javadoc.io](https://www.javadoc.io/)

Can I help?
===========
Yes! Send me your patches and contributions. I will come up with an Contributor License Agreement. 