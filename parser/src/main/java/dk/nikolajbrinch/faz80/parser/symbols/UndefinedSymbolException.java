package dk.nikolajbrinch.faz80.parser.symbols;

public class UndefinedSymbolException extends SymbolException {

  public UndefinedSymbolException(String name, String message) {
    super(name, message);
  }

  public UndefinedSymbolException(String name, String message, Throwable cause) {
    super(name, message, cause);
  }
}
