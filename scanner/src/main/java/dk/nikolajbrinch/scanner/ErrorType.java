package dk.nikolajbrinch.scanner;

public enum ErrorType {
  UNTERMINATED_STRING("Unterminated string");

  private String message;

  private ErrorType(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
