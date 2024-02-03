package dk.nikolajbrinch.faz80.base.errors;

public class BaseException extends  RuntimeException {

  public BaseException(String message) {
    super(message);
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
