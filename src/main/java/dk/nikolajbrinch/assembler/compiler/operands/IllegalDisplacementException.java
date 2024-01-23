package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;

public class IllegalDisplacementException extends RuntimeException {

  private final Object operand;
  private final NumberValue relative;

  public IllegalDisplacementException(Object operand, NumberValue relative, String message) {
    super(message);
    this.operand = operand;
    this.relative = relative;
  }

  public IllegalDisplacementException(Object operand, NumberValue relative, String message, Throwable cause) {
    super(message, cause);
    this.operand = operand;
    this.relative = relative;
  }
}
