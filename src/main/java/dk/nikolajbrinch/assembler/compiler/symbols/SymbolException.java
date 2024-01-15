package dk.nikolajbrinch.assembler.compiler.symbols;

public class SymbolException extends RuntimeException {

  private final String name;

  public SymbolException(String name, String message) {
    super(message);
    this.name = name;
  }

  public SymbolException(String name, String message, Throwable cause) {
    super(message, cause);
    this.name = name;
  }

  public String getSymbolName() {
    return name;
  }
}
