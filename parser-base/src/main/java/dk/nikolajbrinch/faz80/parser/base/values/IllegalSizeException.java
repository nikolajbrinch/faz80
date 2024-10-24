package dk.nikolajbrinch.faz80.parser.base.values;

public class IllegalSizeException extends RuntimeException {

  public IllegalSizeException(String message) {
    super(message);
  }

  public IllegalSizeException(String message, Throwable cause) {
    super(message, cause);
  }
}
