package dk.nikolajbrinch.assembler.compiler;

public class UndefinedVariableException extends AssembleException {

  public UndefinedVariableException(String message) {
    super(message);
  }

  public UndefinedVariableException(String message, Throwable cause) {
    super(message, cause);
  }
}
