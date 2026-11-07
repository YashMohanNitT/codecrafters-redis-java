import java.util.List;

/**
 * One RESP2 value. Requests and responses use the same five types.
 * The first byte on the wire tells which type follows: + - : $ *
 */
public sealed interface RespValue {
  String NULL_BULK_STRING = "$-1\r\n";
  /** "+OK\r\n". Must not contain \r or \n. */
  record SimpleString(String value) implements RespValue {}

  /** "-ERR message\r\n". Must not contain \r or \n. */
  record Error(String message) implements RespValue {}

  /** ":42\r\n" */
  record Integer(long value) implements RespValue {}

  /** "$3\r\nhey\r\n". A null value is the RESP null bulk string "$-1\r\n". */
  record BulkString(String value) implements RespValue {}

  /** "*2\r\n...". A null elements list is the RESP null array "*-1\r\n". */
  record Array(List<RespValue> elements) implements RespValue {}
}
