package dk.nikolajbrinch.assembler.compiler.symbols;

public class UndefinedSymbolException extends SymbolException {

  public UndefinedSymbolException(String name, String message) {
    super(name, message);
  }

  public UndefinedSymbolException(String name, String message, Throwable cause) {
    super(name, message, cause);
  }
}
