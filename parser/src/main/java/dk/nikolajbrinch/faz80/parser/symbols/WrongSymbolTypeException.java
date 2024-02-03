package dk.nikolajbrinch.faz80.parser.symbols;

public class WrongSymbolTypeException extends SymbolException {

  public WrongSymbolTypeException(String name, String message) {
    super(name, message);
  }

  public WrongSymbolTypeException(String name, String message, Throwable cause) {
    super(name, message, cause);
  }
}
