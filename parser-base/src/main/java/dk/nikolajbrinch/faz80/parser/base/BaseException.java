package dk.nikolajbrinch.faz80.parser.base;

public class BaseException extends  RuntimeException {

  public BaseException(String message) {
    super(message);
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
