import java.util.List;

/**
 * The strategy for one command type. The classifier has already checked the arity,
 * so a handler can trust the argument count it was given.
 */
@FunctionalInterface
public interface CommandHandler {
  /** Handles the arguments, without the command name, and returns the reply. */
  RespValue handle(List<String> args);
}
