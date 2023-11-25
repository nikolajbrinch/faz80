package dk.nikolajbrinch.assembler.compiler;

public class AssembleException extends RuntimeException {

  public AssembleException(String message) {
    super(message);
  }

  public AssembleException(String message, Throwable cause) {
    super(message, cause);
  }
}
