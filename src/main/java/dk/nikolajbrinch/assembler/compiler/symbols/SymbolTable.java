package dk.nikolajbrinch.assembler.compiler.symbols;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {

  private final SymbolTable parent;

  private final Map<String, SymbolType> symbolTypes = new HashMap<>();

  private final Map<String, Symbol<?>> symbols = new LinkedHashMap<>();

  public SymbolTable() {
    this(null);
  }

  public SymbolTable(SymbolTable parent) {
    this.parent = parent;
  }

  public <R, T extends Symbol<R>> T get(String name) {
    if (symbols.containsKey(name)) {
      return (T) symbols.get(name);
    }

    if (parent != null) {
      return parent.get(name);
    }

    throw new UndefinedSymbolException(name, "Undefined symbol: " + name);
  }

  public SymbolType getSymbolType(String name) {
    if (symbolTypes.containsKey(name)) {
      return symbolTypes.get(name);
    }

    if (parent != null) {
      return parent.getSymbolType(name);
    }

    throw new UndefinedSymbolException(name, "Undefined symbol: " + name);
  }

  public boolean exists(String name) {
    if (symbols.containsKey(name)) {
      return true;
    }

    if (parent != null) {
      return parent.exists(name);
    }

    return false;
  }

  public <R, T extends Symbol<R>> T assign(String name, SymbolType type, T symbol) {
    if (symbols.containsKey(name)) {
      if (symbolTypes.get(name) == type) {
        symbols.put(name, symbol);
      } else {
        throw new WrongSymbolTypeException(
            name, "Expected symbol type: " + type + ", but was: " + symbolTypes.get(name));
      }

      return symbol;
    }

    if (parent != null) {
      return parent.assign(name, type, symbol);
    }

    throw new UndefinedSymbolException(name, "Undefined symbol: " + name);
  }

  public void define(String name, SymbolType type, Symbol<?> symbol) {
    symbolTypes.put(name, type);
    symbols.put(name, symbol);
  }

  public Map<String, Symbol<?>> getSymbols() {
    return symbols;
  }

  @Override
  public String toString() {
    return symbols.toString();
  }
}
