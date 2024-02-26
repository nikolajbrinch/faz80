package dk.nikolajbrinch.scanner;

public enum ErrorType {
  UNTERMINATED_STRING("Unterminated string"),
  UNTERMINATED_MACRO_BODY("Unterminated Macro body");

  private String message;

  private ErrorType(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
