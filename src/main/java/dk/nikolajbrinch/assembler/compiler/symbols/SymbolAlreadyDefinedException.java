package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.compiler.AssembleException;

public class SymbolAlreadyDefinedException extends AssembleException {

  public SymbolAlreadyDefinedException(String message) {
    super(message);
  }

  public SymbolAlreadyDefinedException(String message, Throwable cause) {
    super(message, cause);
  }
}
