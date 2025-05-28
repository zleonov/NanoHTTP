import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionGetExample {

    public static void main(String[] args) throws IOException {
        final URL resource = new URL("https://www.google.com");
        final HttpURLConnection connection = (HttpURLConnection) resource.openConnection();
        
        connection.setRequestMethod("GET");  // default
        
        try (final InputStream in = connection.getInputStream()) {  
            
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            int n;
            byte[] buffer = new byte[1024];
            while ((n = in.read(buffer, 0, buffer.length)) != -1)
                out.write(buffer, 0, n);
         
            final byte[] bytes = out.toByteArray();
            final String str = new String(bytes); //  platform's default charset
            
            System.out.println(str);
            
        } catch (IOException e) {            
            e.printStackTrace();
        }
        

    }

}
