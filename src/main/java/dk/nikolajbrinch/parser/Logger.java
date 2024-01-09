package dk.nikolajbrinch.parser;


public interface Logger {

  void debug(String message, Object... args);

  void info(String message, Object... args);

  void warn(String message, Object... args);

  void error(String message, Object... args);

}
