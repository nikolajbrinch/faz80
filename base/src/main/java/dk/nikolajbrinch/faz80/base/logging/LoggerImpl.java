package dk.nikolajbrinch.faz80.base.logging;


public enum LoggerImpl implements Logger {
  INSTANCE;

  private Level level = Level.DEBUG;

  @Override
  public void debug(String message, Object... args) {
    if (level == Level.DEBUG) {
      logOut(message, args);
    }
  }

  @Override
  public void info(String message, Object... args) {
    logOut(message, args);
  }

  @Override
  public void warn(String message, Object... args) {
    logErr(message, args);
  }

  @Override
  public void error(String message, Object... args) {
    logErr(message, args);
  }

  private void logOut(String message, Object... args) {
    System.out.println(format(message, args));
  }

  private void logErr(String message, Object... args) {
    System.err.println(format(message, args));
  }

  private String format(String message, Object[] args) {
    return args.length > 0 ? String.format(message, args) : message;
  }

  enum Level {
    DEBUG,
    INFO
  }
}
