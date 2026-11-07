import java.util.List;
import java.util.concurrent.ConcurrentMap;

/** GET key -> the value as a bulk string, or the null bulk string "$-1\r\n" when the key is missing. */
public final class GetHandler implements CommandHandler {
  private final ConcurrentMap<String, String> store;

  public GetHandler(ConcurrentMap<String, String> store) {
    this.store = store;
  }

  @Override
  public RespValue handle(List<String> args) {
    // A missing key gives null, and BulkString(null) encodes as the RESP null bulk string.
    return new RespValue.BulkString(store.get(args.getFirst()));
  }
}
