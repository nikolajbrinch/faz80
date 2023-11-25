package dk.nikolajbrinch.assembler.compiler;

public class IllegalInstructionException extends AssembleException {

  public IllegalInstructionException(String message) {
    super(message);
  }

  public IllegalInstructionException(String message, Throwable cause) {
    super(message, cause);
  }
}
