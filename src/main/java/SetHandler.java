import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

/** SET key value -> +OK. */
public final class SetHandler implements CommandHandler {
  private final ConcurrentMap<String, Value> store;

  public SetHandler(ConcurrentMap<String, Value> store) {
    this.store = store;
  }

  @Override
  public RespValue handle(List<String> args) {
    Long expireAt = System.currentTimeMillis();
    if (args.size() > 2) {
      if (args.get(2).toUpperCase(Locale.ROOT).equals("EX")) {
        expireAt += 1000*Long.parseLong(args.get(3));
      } else if (args.get(2).toUpperCase(Locale.ROOT).equals("PX")) {
        expireAt += Long.parseLong(args.get(3));
      }
    } else {
      expireAt += 999_999_999_999L;
    }
    store.put(args.get(0), new Value(args.get(1), expireAt));
    return new RespValue.SimpleString("OK");
  }
}
