package dk.nikolajbrinch.assembler.ide.ast;

import java.util.function.Supplier;

public record AstTreeValue(int lineNumber, Type type, Supplier<Object> valueSupplier) {

  public enum Type {
    SYMBOL_TABLE,
    NODE
  }

  public AstTreeValue(int lineNumber, Supplier<Object> valueSupplier) {
    this(lineNumber, Type.NODE, valueSupplier);
  }

  @Override
  public String toString() {
    return type == Type.SYMBOL_TABLE ? "SymbolTable" : String.valueOf(valueSupplier.get());
  }
}
