/** A request the server rejects. The message is sent to the client as a RESP error, for example "ERR syntax error". */
public class CommandException extends RuntimeException {
  public CommandException(String message) {
    // RESP errors end at the first \r\n, so a client-supplied name must not add one.
    super(message.replace('\r', ' ').replace('\n', ' '));
  }
}
