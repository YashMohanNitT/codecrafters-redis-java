import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Decodes RESP2 bytes from a stream into RespValues, one complete value per decode() call.
 * Reads byte by byte as needed, so a value split across TCP packets, or two values in one
 * packet, are both handled.
 */
public class RespDecoder {
  private final InputStream in;

  public RespDecoder(InputStream in) {
    this.in = new BufferedInputStream(in);
  }

  /** Reads one complete value. Returns null when the stream ended cleanly before a new value. */
  public RespValue decode() throws IOException {
    int type = in.read();
    if (type == -1) {
      return null;
    }
    return decodeAfterType(type);
  }

  // Reads a value that must be there, for example an array element.
  private RespValue decodeRequired() throws IOException {
    int type = in.read();
    if (type == -1) {
      throw new EOFException("Stream ended inside an array");
    }
    return decodeAfterType(type);
  }

  private RespValue decodeAfterType(int type) throws IOException {
    return switch (type) {
      case '+' -> new RespValue.SimpleString(readLine());
      case '-' -> new RespValue.Error(readLine());
      case ':' -> new RespValue.Integer(readLong());
      case '$' -> readBulkString();
      case '*' -> readArray();
      default -> throw new IOException("Unknown RESP type byte: '" + (char) type + "'");
    };
  }

  // Called after '$'. Stream: "3\r\nhey\r\n" or "-1\r\n"
  private RespValue.BulkString readBulkString() throws IOException {
    long length = readLong();
    if (length == -1) {
      return new RespValue.BulkString(null);
    }
    if (length < 0 || length > Integer.MAX_VALUE) {
      throw new IOException("Invalid bulk string length: " + length);
    }
    byte[] data = in.readNBytes((int) length);
    if (data.length < length) {
      throw new EOFException("Stream ended inside a bulk string");
    }
    if (in.read() != '\r' || in.read() != '\n') {
      throw new IOException("Bulk string is not followed by \\r\\n");
    }
    return new RespValue.BulkString(new String(data, StandardCharsets.UTF_8));
  }

  // Called after '*'. Stream: "2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n" or "-1\r\n"
  private RespValue.Array readArray() throws IOException {
    long count = readLong();
    if (count == -1) {
      return new RespValue.Array(null);
    }
    if (count < 0 || count > Integer.MAX_VALUE) {
      throw new IOException("Invalid array length: " + count);
    }
    List<RespValue> elements = new ArrayList<>();
    for (long i = 0; i < count; i++) {
      elements.add(decodeRequired());
    }
    return new RespValue.Array(elements);
  }

  private long readLong() throws IOException {
    String line = readLine();
    try {
      return Long.parseLong(line);
    } catch (NumberFormatException e) {
      throw new IOException("Expected a number but got: " + line, e);
    }
  }

  // Reads bytes up to "\r\n" and returns them without the "\r\n".
  private String readLine() throws IOException {
    ByteArrayOutputStream line = new ByteArrayOutputStream();
    int b;
    while ((b = in.read()) != '\r') {
      if (b == -1) {
        throw new EOFException("Stream ended inside a line");
      }
      line.write(b);
    }
    if (in.read() != '\n') {
      throw new IOException("Expected \\n after \\r");
    }
    return line.toString(StandardCharsets.UTF_8);
  }
}
