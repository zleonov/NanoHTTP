import java.io.IOException;
import java.net.URL;

import software.leonov.client.http.HttpClient;
import software.leonov.client.http.HttpRequest;
import software.leonov.client.http.HttpResponse;
import software.leonov.client.http.HttpResponseException;
import software.leonov.client.http.ResponseBody;

public class NanoHTTPGetExample {

    public static void main(String[] args) throws IOException {
        final URL resource = new URL("https://www.google.com/abc");

        final HttpClient http = HttpClient.defaultClient();
        final HttpRequest request = http.get(resource);

        try (final HttpResponse response = request.send()) {
            final ResponseBody body = response.getBody();
            final String str = body.asString();
            System.out.println(str);
        } catch (final HttpResponseException e) {
            System.err.println(e.getMessage()); // the status-line returned by the server
            System.err.println(e.getServerReponse().asString()); // the response message returned by the server
        }
//
//        
//        try (final HttpResponse response = HttpClient.defaultClient().get(resource).send()){
//            final String text = response.getBody().asString();
//            System.out.println(text);
//        } catch (final HttpResponseException e) {
//            System.err.println(e.getMessage()); // the status-line returned by the server
//            System.err.println(e.getErrorMessage()); // the response message returned by the server
//        }
//        
//        final String text = HttpClient.defaultClient().get(resource).send().getBody().asString();
    }

}
