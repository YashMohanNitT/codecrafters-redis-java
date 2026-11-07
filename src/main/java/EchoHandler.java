import java.util.List;

/** ECHO message -> the message as a bulk string. */
public final class EchoHandler implements CommandHandler {
  @Override
  public RespValue handle(List<String> args) {
    return new RespValue.BulkString(args.getFirst());
  }
}
