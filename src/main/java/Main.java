import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
  private static final int PORT = 6379;
  private static final byte[] PONG = "+PONG\r\n".getBytes(StandardCharsets.UTF_8);

  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    try (ServerSocket serverSocket = new ServerSocket()) {
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors.
      // It only takes effect when set before bind().
      serverSocket.setReuseAddress(true);
      serverSocket.bind(new InetSocketAddress(PORT));

      while (true) {
        // Wait for connection from client. Each client gets its own socket and its own thread.
        Socket clientSocket = serverSocket.accept();
        new Thread(() -> handleClient(clientSocket)).start();
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  // Replies +PONG to every read until the client closes the connection, then closes the socket.
  private static void handleClient(Socket clientSocket) {
    try (clientSocket) {
      InputStream in = clientSocket.getInputStream();
      OutputStream out = clientSocket.getOutputStream();
      final byte[] buffer = new byte[1024];
      int n;
      while ((n = in.read(buffer)) != -1) {
        String message = new String(buffer, 0, n, StandardCharsets.UTF_8);
        System.out.println(message.replace("\r", "\\r").replace("\n", "\\n"));
        out.write(PONG);
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
