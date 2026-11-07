import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Classifies each request and hands it to the strategy for its type.
 * One instance is shared by all client threads, so the store must be thread-safe.
 */
public final class CommandDispatcher {
  private final Map<CommandType, CommandHandler> handlers = new EnumMap<>(CommandType.class);

  public CommandDispatcher() {
    ConcurrentMap<String, String> store = new ConcurrentHashMap<>();
    for (CommandType type : CommandType.values()) {
      // No default branch: a new CommandType does not compile until it gets a handler here.
      handlers.put(type, switch (type) {
        case PING -> new PingHandler();
        case ECHO -> new EchoHandler();
        case SET -> new SetHandler(store);
        case GET -> new GetHandler(store);
      });
    }
  }

  /** Returns the reply for one request. A rejected request gets a RESP error, and the connection stays open. */
  public RespValue respond(RespValue request) {
    try {
      Command command = CommandClassifier.classify(request);
      return handlers.get(command.type()).handle(command.args());
    } catch (CommandException e) {
      return new RespValue.Error(e.getMessage());
    }
  }
}
