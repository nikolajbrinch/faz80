package dk.nikolajbrinch.assembler.compiler.values;

public class IllegalLogicOperationException extends RuntimeException {

  public IllegalLogicOperationException(String message) {
    super(message);
  }

  public IllegalLogicOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
