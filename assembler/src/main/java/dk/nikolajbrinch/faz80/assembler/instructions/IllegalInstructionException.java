package dk.nikolajbrinch.faz80.assembler.instructions;

public class IllegalInstructionException extends RuntimeException {

  public IllegalInstructionException(String message) {
    super(message);
  }

  public IllegalInstructionException(String message, Throwable cause) {
    super(message, cause);
  }
}
