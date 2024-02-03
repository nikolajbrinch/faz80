package dk.nikolajbrinch.faz80.base.logging;

public class LoggerFactory {

  public static Logger getLogger() {
    return LoggerImpl.INSTANCE;
  }
}
