import java.io.IOException;
import java.net.URL;

import software.leonov.client.http.ByteArrayBody;
import software.leonov.client.http.HttpClient;
import software.leonov.client.http.HttpRequest;
import software.leonov.client.http.HttpResponse;
import software.leonov.client.http.HttpResponseException;
import software.leonov.client.http.ResponseBody;

public class NanoHTTPPostExample {

    public static void main(String[] args) throws IOException {
        final URL resource = new URL("https://www.google.com/abc");

        final ByteArrayBody body = ByteArrayBody.encode("Hello, World!");

        final HttpClient client = HttpClient.defaultClient();
        // final HttpResponse response = client.post(resource).setBody(body).setContentType("text/plain; charset=utf-8").send();

        try (final HttpResponse response = client.post(resource).setBody(body).setContentType("text/plain; charset=utf-8").send()) {}
    }
}