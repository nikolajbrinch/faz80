package dk.nikolajbrinch.assembler.compiler;

public class IllegalSizeException extends AssembleException {

  public IllegalSizeException(String message) {
    super(message);
  }

  public IllegalSizeException(String message, Throwable cause) {
    super(message, cause);
  }
}
