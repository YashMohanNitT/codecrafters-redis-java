import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/** Encodes RespValues into RESP2 bytes. */
public final class RespEncoder {
  private static final byte[] CRLF = {'\r', '\n'};

  private RespEncoder() {}

  public static byte[] encode(RespValue value) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    write(value, out);
    return out.toByteArray();
  }

  private static void write(RespValue value, ByteArrayOutputStream out) {
    switch (value) {
      case RespValue.SimpleString s -> writeLine('+', requireSingleLine(s.value()), out);
      case RespValue.Error e -> writeLine('-', requireSingleLine(e.message()), out);
      case RespValue.Integer i -> writeLine(':', Long.toString(i.value()), out);
      case RespValue.BulkString b -> {
        if (b.value() == null) {
          writeLine('$', "-1", out);
          return;
        }
        // The length is in bytes, not characters: "é" is 1 character but 2 UTF-8 bytes.
        byte[] data = b.value().getBytes(StandardCharsets.UTF_8);
        writeLine('$', Integer.toString(data.length), out);
        out.writeBytes(data);
        out.writeBytes(CRLF);
      }
      case RespValue.Array a -> {
        if (a.elements() == null) {
          writeLine('*', "-1", out);
          return;
        }
        writeLine('*', Integer.toString(a.elements().size()), out);
        for (RespValue element : a.elements()) {
          write(element, out);
        }
      }
    }
  }

  private static void writeLine(char type, String text, ByteArrayOutputStream out) {
    out.write(type);
    out.writeBytes(text.getBytes(StandardCharsets.UTF_8));
    out.writeBytes(CRLF);
  }

  // Simple strings and errors end at the first \r\n, so they cannot contain one.
  private static String requireSingleLine(String text) {
    if (text.indexOf('\r') >= 0 || text.indexOf('\n') >= 0) {
      throw new IllegalArgumentException("Simple strings and errors cannot contain \\r or \\n: " + text);
    }
    return text;
  }
}
