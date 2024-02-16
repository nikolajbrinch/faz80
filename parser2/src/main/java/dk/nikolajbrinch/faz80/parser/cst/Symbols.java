package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.IdentifierUtil;
import java.util.HashMap;
import java.util.Map;

public class Symbols {
  private final Map<String, SymbolInfo> symbols = new HashMap<>();
  private final Symbols parent;

  public Symbols(Symbols parent) {
    this.parent = parent;
  }

  void define(String name, SymbolType type) {
    symbols.put(IdentifierUtil.normalize(name), new SymbolInfo(name, type));
  }

  SymbolInfo lookup(String name) {
    SymbolInfo info = symbols.get(IdentifierUtil.normalize(name));

    if (info != null) {
      return info;
    } else if (parent != null) {
      return parent.lookup(name);
    }

    return null; // Symbol not found in any enclosing scopes
  }
}

record SymbolInfo(String name, SymbolType type) {}

enum SymbolType {
  MACRO,
  LABEL,
  INSTRUCTION,
  VARIABLE,
  CONSTANT,

  GLOBAL,
  UNDEFINED
}
