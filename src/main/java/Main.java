import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    //  Uncomment the code below to pass the first stage
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
          serverSocket = new ServerSocket(port);
          // Since the tester restarts your program quite often, setting SO_REUSEADDR
          // ensures that we don't run into 'Address already in use' errors
          serverSocket.setReuseAddress(true);
          // Wait for connection from client.
          clientSocket = serverSocket.accept();
          InputStream inputStream = clientSocket.getInputStream();
          byte[] buffer = new byte[1024];
          int n;
          while ((n = inputStream.read(buffer)) != -1) {
              String message = new String(buffer, 0, n);
              System.out.println(message.replace("\r", "\\r").replace("\n", "\\n"));
              byte[] bytes = "+PONG\r\n".getBytes(StandardCharsets.UTF_8);
              clientSocket.getOutputStream().write(bytes);
          }
          /*String output = "+PONG\r\n";*/
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        } finally {
          try {
            if (clientSocket != null) {
              clientSocket.close();
            }
          } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
          }
        }
  }
}
