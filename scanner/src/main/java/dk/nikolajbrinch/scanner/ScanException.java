package dk.nikolajbrinch.scanner;

public class ScanException extends RuntimeException {

  private final ScanError error;

  public ScanException(ScanError error) {
    this.error = error;
  }

  public ScanException(ScanError error, Throwable cause) {
    super(cause);
    this.error = error;
  }

  public ScanError getError() {
    return error;
  }
}
