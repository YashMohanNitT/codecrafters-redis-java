import java.util.List;
import java.util.concurrent.ConcurrentMap;

/** GET key -> the value as a bulk string, or the null bulk string "$-1\r\n" when the key is missing. */
public final class GetHandler implements CommandHandler {
  private final ConcurrentMap<String, Value> store;

  public GetHandler(ConcurrentMap<String, Value> store) {
    this.store = store;
  }

  @Override
  public RespValue handle(List<String> args) {
    // A missing key gives null, and BulkString(null) encodes as the RESP null bulk string.
    Value value = store.get(args.getFirst());
    if (value.expireAt >= System.currentTimeMillis()) {
      return new RespValue.BulkString(value.value);
    }
    store.remove(args.getFirst());
    return new RespValue.BulkString(null);
  }
}
