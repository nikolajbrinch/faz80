package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;

public class IllegalDisplacementException extends RuntimeException {

  private final NumberValue relative;

  public IllegalDisplacementException(NumberValue relative, String message) {
    super(message);
    this.relative = relative;
  }

  public IllegalDisplacementException(NumberValue relative, String message, Throwable cause) {
    super(message, cause);
    this.relative = relative;
  }
}
