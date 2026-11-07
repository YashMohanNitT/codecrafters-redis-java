import java.util.List;

/** PING -> +PONG. PING message -> the message as a bulk string. */
public final class PingHandler implements CommandHandler {
  @Override
  public RespValue handle(List<String> args) {
    return switch (args.size()) {
      case 0 -> new RespValue.SimpleString("PONG");
      case 1 -> new RespValue.BulkString(args.getFirst());
      default -> throw new CommandException("ERR wrong number of arguments for 'ping' command");
    };
  }
}
