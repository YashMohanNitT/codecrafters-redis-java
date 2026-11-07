import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** The commands this server understands, with the number of parts each one needs. */
public enum CommandType {
  PING(1),
  ECHO(2),
  SET(3),
  GET(2);

  private static final Map<String, CommandType> BY_NAME =
      Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(Enum::name, Function.identity()));

  // Redis-style arity. It counts the command name too, so "GET foo" has 2 parts.
  // A positive arity means exactly that many parts: GET is 2.
  // A negative arity means at least that many: SET is -3, because "SET key value"
  // needs 3 parts and options such as "PX 100" may follow.
  private final int arity;

  CommandType(int arity) {
    this.arity = arity;
  }

  /** Finds the type for a command name, ignoring case: "get" and "GET" both match GET. */
  public static Optional<CommandType> fromName(String name) {
    return Optional.ofNullable(BY_NAME.get(name.toUpperCase(Locale.ROOT)));
  }

  /** Returns true when a request with this many parts, the name included, fits this command's arity. */
  public boolean hasValidArity(int partCount) {
    return partCount >= this.arity;
  }
}
