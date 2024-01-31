package dk.nikolajbrinch.assembler.compiler.instructions;

public class InstructionException extends RuntimeException {

  public InstructionException(String message) {
    super(message);
  }

  public InstructionException(String message, Throwable cause) {
    super(message, cause);
  }
}
