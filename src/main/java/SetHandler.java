import java.util.List;
import java.util.concurrent.ConcurrentMap;

/** SET key value -> +OK. */
public final class SetHandler implements CommandHandler {
  private final ConcurrentMap<String, String> store;

  public SetHandler(ConcurrentMap<String, String> store) {
    this.store = store;
  }

  @Override
  public RespValue handle(List<String> args) {
    // Options such as "PX 100" are not supported yet. Reject them instead of silently ignoring them.
    if (args.size() > 2) {
      throw new CommandException("ERR syntax error");
    }
    store.put(args.get(0), args.get(1));
    return new RespValue.SimpleString("OK");
  }
}
