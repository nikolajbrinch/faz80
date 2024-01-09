package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.compiler.AssembleException;

public class WrongSymbolTypeException extends AssembleException {

  public WrongSymbolTypeException(String message) {
    super(message);
  }

  public WrongSymbolTypeException(String message, Throwable cause) {
    super(message, cause);
  }
}
