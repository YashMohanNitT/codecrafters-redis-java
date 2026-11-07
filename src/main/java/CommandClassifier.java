import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Turns a decoded request such as ["SET", "foo", "bar"] into a Command, or rejects it. */
public final class CommandClassifier {
  private static final String NOT_A_COMMAND = "ERR Protocol error: expected a non-empty array of bulk strings";

  private CommandClassifier() {}

  public static Command classify(RespValue request) {
    if (!(request instanceof RespValue.Array(List<RespValue> elements)) || elements == null || elements.isEmpty()) {
      throw new CommandException(NOT_A_COMMAND);
    }
    List<String> parts = new ArrayList<>(elements.size());
    for (RespValue element : elements) {
      if (!(element instanceof RespValue.BulkString(String value)) || value == null) {
        throw new CommandException(NOT_A_COMMAND);
      }
      parts.add(value);
    }

    String name = parts.getFirst();
    CommandType type = CommandType.fromName(name)
        .orElseThrow(() -> new CommandException("ERR unknown command '" + name + "'"));
    if (!type.hasValidArity(parts.size())) {
      throw new CommandException(
          "ERR wrong number of arguments for '" + name.toLowerCase(Locale.ROOT) + "' command");
    }
    return new Command(type, List.copyOf(parts.subList(1, parts.size())));
  }
}
