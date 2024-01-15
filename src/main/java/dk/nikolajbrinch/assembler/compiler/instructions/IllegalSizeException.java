package dk.nikolajbrinch.assembler.compiler.instructions;

public class IllegalSizeException extends InstructionException {

  public IllegalSizeException(String message) {
    super(message);
  }

  public IllegalSizeException(String message, Throwable cause) {
    super(message, cause);
  }
}
