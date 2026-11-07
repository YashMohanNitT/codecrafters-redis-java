import java.util.List;

/** A classified request: its type and its arguments, without the command name. "SET foo bar" -> (SET, [foo, bar]). */
public record Command(CommandType type, List<String> args) {}
