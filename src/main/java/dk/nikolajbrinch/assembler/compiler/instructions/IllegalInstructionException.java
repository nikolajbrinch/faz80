package dk.nikolajbrinch.assembler.compiler.instructions;

public class IllegalInstructionException extends InstructionException {

  public IllegalInstructionException(String message) {
    super(message);
  }

  public IllegalInstructionException(String message, Throwable cause) {
    super(message, cause);
  }
}
