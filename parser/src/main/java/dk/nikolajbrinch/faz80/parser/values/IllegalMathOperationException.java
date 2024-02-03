package dk.nikolajbrinch.faz80.parser.values;

public class IllegalMathOperationException extends RuntimeException {

  public IllegalMathOperationException(String message) {
    super(message);
  }

  public IllegalMathOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
