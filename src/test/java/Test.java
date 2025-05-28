import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

import org.apache.http.HttpException;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.http.javanet.NetHttpTransport;

import software.leonov.client.http.ByteArrayBody;
import software.leonov.client.http.FileBody;
import software.leonov.client.http.HttpClient;
import software.leonov.client.http.HttpRequest;
import software.leonov.client.http.HttpResponseException;
import software.leonov.client.http.MultipartBody;
import software.leonov.client.http.RequestBody;

public class Test {


    public static void main(String[] args) throws MalformedURLException, IOException, HttpException {
        
   //     final String address = "https://www.google.com/";
        
        //testGoogleMultipart();
        try {
        testMineMultipart();
        }catch(HttpResponseException e  ) {
            System.out.println(e);
            System.out.println(e.headers());
        }
        
        
//        try (final HttpResponse response = HttpClient.defaultClient().get(new URL(address)).send()){
//            System.out.println(response.getBody().asString());
//        } catch (final Exception e) {
//            e.printStackTrace();
//        }
        
        
//        final Path song = Paths.get("TODO");
//        try (final HttpResponse response = HttpClient.defaultClient()
//                                                     .post(new URL(address))
//                                                     .setContentType("audio/x-flac; rate=16000")
//                                                     .setContentEncoding("gzip")
//                .setBody(GZipEncoding.stream(() -> Files.newInputStream(song))).send())
//        {
//                System.out.println(response.getStatusLine());
//        }
//        
//        final Path song = Paths.get("TODO");
//        try (final HttpResponse response = HttpClient.defaultClient()
//                .post(new URL(address))
//                .setContentType("audio/x-flac; rate=16000")
//                .setContentEncoding("gzip")
//                .setBody(GZipEncoding.stream(() -> Files.newInputStream(song)))
//                .send())
//{
//System.out.println(response.getStatusLine());
//}
//        
//        
        
//        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
//
//        com.google.api.client.http.HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(address + "xyz"));
       // com.google.api.client.http.HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(address), new FileContent("audio/x-flac; rate=16000", song.toFile()).setCloseInputStream(true));

        
//        com.google.api.client.http.HttpResponse response = request.execute();
        
        
        
//        request.setHeaders(new com.google.api.client.http.HttpHeaders().set("accept", "foo"));
//        
//        request.getHeaders().forEach((i, j) -> System.out.println(i + ": " + j));
        
    //    testGoogle();
    //    System.out.println("******");
    //   testMine();
  //      HttpResponse response = HttpRequest.get(new URL("https://api.github.com/users")).setFollowRedirects(true).setAcceptGZipCompression(true).setUseCaches(false).setKeepAlive(true).send();
        //
        // response.getHeaders().forEach((i, j) -> System.out.println(i + ": " + j));
        // System.out.println(response.getHeaders().keySet().iterator().next());

        // response.getRequestProperties().forEach((i, j) -> System.out.println(i + ": " + j));

        // System.out.println(response.getContentEncoding());
//HttpURLConnection conn = (HttpURLConnection) new URL("https://api.github.com/users").openConnection();
//conn.setRequestProperty("Content-Length", "123");
//System.out.println(conn.getContentLengthLong());
        //System.out.println(response.getContentCharset());
        //System.out.println(response.getMessageBodyAsString());
        // System.out.println(response.getResponseHeaders());

        // System.out.println(new URL("http://square.github.io/").equals(new URL("http://google.github.io/")));

    }
    
    
    public static void testMineMultipart() throws IOException {
        HttpClient client = HttpClient.defaultClient();
        
        RequestBody b1 = ByteArrayBody.encode("abc");
        
        FileBody body = new FileBody(Paths.get("pom.xml"));
      //  System.out.println(body.getContentType());
        
        client.post(new URL("http://localhost:80")).setBody(MultipartBody.formData()
                .field("abc", b1)
                .field("abc", "xyz")
                .file("filefield", body.filename(), body)
                .build()).send();
        
    }
    
    public static void testMine() throws IOException {        
        
        
        HttpRequest request = HttpClient.builder()
                .setFollowRedirects(false)
                .setUseCaches(false)
                .setReadTimeout(Duration.ofSeconds(25))
                .setConnectTimeout(Duration.ofSeconds(22))
                .setUserAgent("foobar")
                .build()
                
                
                
                
                .head(new URL("http://localhost:80"));
        
//        System.out.println(request.headerValue("accept-encoding"));
//        System.out.println(request.followRedirects());
//        System.out.println(request.useCaches());
//        System.out.println(request.ifModifiedSince());
//        System.out.println(request.readTimeout());
//        System.out.println(request.connectTimeout());
//        System.out.println(request.userAgent());
        
        

        
//    //    System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
//        HttpRequest request = HttpRequest.head(new URL("http://localhost:80"))
//                .acceptDeflateCompression()
//                .acceptGZipEncodingCompression()                
//                .setContentEncoding("gzip")
//                .setContentType("text/plain charset=utf8")
//                .setIfModifiedSince(635465465)
//                .setKeepAlive(true)
//                .setUseCaches(true)
//                .userAgent("abc")
//        
//                .setContentLength(5);
//        
      //  System.out.println(request.getRequestHeaders());
   //     request.getRequestHeaders().forEach((name, values) -> values.forEach(value -> System.out.println(name + ": " + value)));
        request.headers().forEach((name, value) -> System.out.println(name + ": " + value));        
        request.send();
        
        System.out.println();
        
      //  request.setMessageBody(() -> Files.newInputStream(Paths.get("c:/foo.txt")));
        
     //  request.getRequestHeaders().forEach((name, values) -> values.forEach(value -> System.out.println(name + ": " + value)));
        request.headers().forEach((name, value) -> System.out.println(name + ": " + value));
        
        
        
        //System.out.println(request.getRequestHeaders());
        //HttpResponse response = request.send();
      //  System.out.println(response.getMessageBody());
    }
    
    public static void testGoogle() throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        com.google.api.client.http.HttpRequest request = requestFactory.buildHeadRequest(new GenericUrl("http://localhost:80"));
        
        request.setHeaders(new com.google.api.client.http.HttpHeaders().set("accept", "foo"));
        
        request.getHeaders().forEach((i, j) -> System.out.println(i + ": " + j));
        
        //request.execute();
        
        //request.getHeaders().forEach((i, j) -> System.out.println(i + ": " + j));
        
      //  System.out.println(request.getResponseHeaders().get("Accept"));
        String rawResponse = request.execute().parseAsString();
       // System.out.println(rawResponse);
    }
    
    public static void testGoogleMultipart() throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        
        MultipartContent content = new MultipartContent();
        content.addPart(new MultipartContent.Part().setContent(ByteArrayContent.fromString(null, "abc")));
        content.addPart(new MultipartContent.Part().setContent(new AbstractInputStreamContent(null) {
            
            @Override
            public boolean retrySupported() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public long getLength() throws IOException {
                // TODO Auto-generated method stub
                return -1;
            }
            
            @Override
            public InputStream getInputStream() throws IOException {
               return new FileInputStream(new File("TODO"));
            }
        })
        //        .setHeaders(new HttpHeaders().setAccept("foobar"))
        );
        
        
        com.google.api.client.http.HttpRequest request = requestFactory.buildPostRequest(new GenericUrl("http://localhost:80"), content);
        
        
        
        
        
        request.setHeaders(new com.google.api.client.http.HttpHeaders().set("accept", "foo"));
        
        request.getHeaders().forEach((i, j) -> System.out.println(i + ": " + j));
        
        request.execute();
//        com.google.api.client.http.HttpRequest request = requestFactory.buildGetRequest(new GenericUrl("http://github.com/n1k0/casperjs/")).setFollowRedirects(false);
//        
//        System.out.println(
//        request.execute()
//        .isSuccessStatusCode()
//        )
//        
//        ;
//        request.getHeaders().forEach((i, j) -> System.out.println(i + ": " + j));
//        
//        System.out.println(request.getResponseHeaders().get("Accept"));
//        String rawResponse = request.execute().parseAsString();
//        System.out.println(rawResponse);
    }
    
    
}
