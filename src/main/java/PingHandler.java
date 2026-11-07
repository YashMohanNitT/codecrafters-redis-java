import java.util.List;

/** PING -> +PONG. PING message -> the message as a bulk string. */
public final class PingHandler implements CommandHandler {
  @Override
  public RespValue handle(List<String> args) {
    return new RespValue.BulkString("PONG");
  }
}
