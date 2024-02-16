package dk.nikolajbrinch.faz80.parser.cst;

public class CstParseException extends RuntimeException {
  public CstParseException(String message) {
    super(message);
  }

  public CstParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
