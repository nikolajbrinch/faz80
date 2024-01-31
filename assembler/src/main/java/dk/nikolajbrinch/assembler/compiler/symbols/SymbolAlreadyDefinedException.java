package dk.nikolajbrinch.assembler.compiler.symbols;

public class SymbolAlreadyDefinedException extends SymbolException {

  public SymbolAlreadyDefinedException(String name, String message) {
    super(name, message);
  }

  public SymbolAlreadyDefinedException(String name, String message, Throwable cause) {
    super(name, message, cause);
  }
}
