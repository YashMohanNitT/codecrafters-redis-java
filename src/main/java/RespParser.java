import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Decodes RESP bytes from a client into Java values.
 */
public class RespParser {
  private final InputStream in;

  public RespParser(InputStream in) {
    this.in = new BufferedInputStream(in);
  }

  // Reads one complete RESP value: reads the type byte, then hands off to the right helper.
  // Example input: "*2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n"  ->  ["ECHO", "hey"]
  public Object parse() throws IOException {
    throw new UnsupportedOperationException("parse is not written yet");
  }

  // Called after '*' was read. Input left in the stream: "2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n"
  private List<Object> readArray() throws IOException {
    throw new UnsupportedOperationException("readArray is not written yet");
  }

  // Called after '$' was read. Input left in the stream: "4\r\nECHO\r\n"
  private String readBulkString() throws IOException {
    throw new UnsupportedOperationException("readBulkString is not written yet");
  }

  // Reads bytes up to "\r\n" and returns them without the "\r\n".
  // Input "2\r\n$4..."  ->  returns "2", and the stream now starts at "$4..."
  private String readLine() throws IOException {
    // TODO(human)
    throw new UnsupportedOperationException("readLine is not written yet");
  }
}
