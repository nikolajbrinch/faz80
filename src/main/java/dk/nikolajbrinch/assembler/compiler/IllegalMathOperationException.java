package dk.nikolajbrinch.assembler.compiler;

public class IllegalMathOperationException extends AssembleException {

  public IllegalMathOperationException(String message) {
    super(message);
  }

  public IllegalMathOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
