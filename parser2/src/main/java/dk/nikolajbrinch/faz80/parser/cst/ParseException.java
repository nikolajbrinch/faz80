package dk.nikolajbrinch.faz80.parser.cst;

public class ParseException extends RuntimeException {
  public ParseException(String message) {
    super(message);
  }

  public ParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
