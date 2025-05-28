import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;

import software.leonov.client.http.HttpClient;
import software.leonov.client.http.HttpRequest;
import software.leonov.client.http.HttpResponse;
import software.leonov.client.http.ResponseBody;

public class GoogleHttpClientGetExample {

    public static void main(String[] args) throws IOException {
        final URL resource = new URL("https://www.google.com");

        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
      com.google.api.client.http.HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(resource));
      
      
      
      String rawResponse = request.execute().parseAsString();

//        try (final HttpResponse response = request.send()) {
//            final ResponseBody body = response.getBody();
//            final String str = body.asString();
//            System.out.println(str);
//        } catch (HttpResponseException e) {
//            e.printStackTrace();
//        }

    }

}
