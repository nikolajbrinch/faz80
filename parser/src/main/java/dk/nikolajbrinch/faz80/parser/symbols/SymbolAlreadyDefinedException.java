package dk.nikolajbrinch.faz80.parser.symbols;

import dk.nikolajbrinch.faz80.parser.symbols.SymbolException;

public class SymbolAlreadyDefinedException extends SymbolException {

  public SymbolAlreadyDefinedException(String name, String message) {
    super(name, message);
  }

  public SymbolAlreadyDefinedException(String name, String message, Throwable cause) {
    super(name, message, cause);
  }
}
