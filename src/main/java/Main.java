import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  private static final int PORT = 6379;

  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    try (ServerSocket serverSocket = new ServerSocket()) {
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors.
      // It only takes effect when set before bind().
      serverSocket.setReuseAddress(true);
      serverSocket.bind(new InetSocketAddress(PORT));

      // One dispatcher for all clients, so they all share the same keys.
      CommandDispatcher dispatcher = new CommandDispatcher();
      while (true) {
        // Wait for connection from client. Each client gets its own socket and its own thread.
        Socket clientSocket = serverSocket.accept();
        new Thread(() -> handleClient(clientSocket, dispatcher)).start();
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  // Decodes one request at a time and writes one reply for each, until the client disconnects.
  private static void handleClient(Socket clientSocket, CommandDispatcher dispatcher) {
    try (clientSocket) {
      RespDecoder decoder = new RespDecoder(clientSocket.getInputStream());
      OutputStream out = clientSocket.getOutputStream();
      RespValue request;
      while ((request = decoder.decode()) != null) {
        System.out.println("Received: " + request);
        out.write(RespEncoder.encode(dispatcher.respond(request)));
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
