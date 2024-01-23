package dk.nikolajbrinch.assembler.compiler.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SymbolTable {

  private final SymbolTable parent;

  private final Map<String, SymbolType> symbolTypes = new HashMap<>();

  private final Map<String, List<Optional<?>>> symbols = new LinkedHashMap<>();

  public SymbolTable() {
    this(null);
  }

  public SymbolTable(SymbolTable parent) {
    this.parent = parent;
  }

  public Optional<?> get(String name) {
    String normalizedName = normalize(name);

    if (symbolTypes.containsKey(normalizedName)) {
      List<Optional<?>> values = symbols.get(normalizedName);

      return values == null ? null : values.getFirst();
    }

    if (parent != null) {
      return parent.get(normalizedName);
    }

    throw new UndefinedSymbolException(name, "Undefined symbol: " + normalizedName);
  }

  public SymbolType getSymbolType(String name) {
    String normalizedName = normalize(name);

    if (symbolTypes.containsKey(normalizedName)) {
      return symbolTypes.get(normalizedName);
    }

    if (parent != null) {
      return parent.getSymbolType(normalizedName);
    }

    throw new UndefinedSymbolException(normalizedName, "Undefined symbol: " + normalizedName);
  }

  public boolean exists(String name) {
    String normalizedName = normalize(name);

    if (symbolTypes.containsKey(normalizedName)) {
      return true;
    }

    if (parent != null) {
      return parent.exists(normalizedName);
    }

    return false;
  }

  public Optional<?> assign(String name, SymbolType type, Optional<?> symbol) {
    String normalizedName = normalize(name);

    SymbolType symbolType = symbolTypes.get(normalizedName);

    if (symbolType != null) {
      if (symbolType == type) {
        addSymbol(normalizedName, symbol);
      } else {
        throw new WrongSymbolTypeException(
            normalizedName,
            "Expected symbol type: " + type + ", but was: " + symbolTypes.get(normalizedName));
      }

      return symbol;
    }

    if (parent != null) {
      return parent.assign(normalizedName, type, symbol);
    }

    throw new UndefinedSymbolException(name, "Undefined symbol: " + normalizedName);
  }

  public void define(String name, SymbolType type) {
    define(name, type, null);
  }

  public void define(String name, SymbolType type, Optional<?> symbol) {
    String normalizedName = normalize(name);

    symbolTypes.put(normalizedName, type);
    addSymbol(normalizedName, symbol);
  }

  public Map<String, List<Optional<?>>> getSymbols() {
    return symbols;
  }

  @Override
  public String toString() {
    return symbols.toString();
  }

  private void addSymbol(String name, Optional<?> symbol) {
    if (symbol != null) {
      List<Optional<?>> optionals = symbols.get(name);

      if (optionals == null) {
        optionals = new ArrayList<>(List.of(symbol));
        symbols.put(name, optionals);
      } else {
        optionals.addFirst(symbol);
      }
    }
  }

  private String normalize(String name) {
    return name.replaceAll("^(?:[.]*?)([^.]+?)(?:\\:*?)$", "$1");
  }
}
