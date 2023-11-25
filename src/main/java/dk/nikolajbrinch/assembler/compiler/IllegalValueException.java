package dk.nikolajbrinch.assembler.compiler;

public class IllegalValueException extends AssembleException {

  public IllegalValueException(String message) {
    super(message);
  }

  public IllegalValueException(String message, Throwable cause) {
    super(message, cause);
  }
}
