package dk.nikolajbrinch.faz80.assembler.operands;

import dk.nikolajbrinch.faz80.parser.values.NumberValue;

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
