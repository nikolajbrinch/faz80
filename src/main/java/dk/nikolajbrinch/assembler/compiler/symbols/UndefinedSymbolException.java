package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.compiler.AssembleException;

public class UndefinedSymbolException extends AssembleException {

  public UndefinedSymbolException(String message) {
    super(message);
  }

  public UndefinedSymbolException(String message, Throwable cause) {
    super(message, cause);
  }
}
