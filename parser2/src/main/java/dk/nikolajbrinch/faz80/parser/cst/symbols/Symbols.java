package dk.nikolajbrinch.faz80.parser.cst.symbols;

import dk.nikolajbrinch.faz80.parser.IdentifierUtil;
import dk.nikolajbrinch.faz80.parser.cst.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Symbols {
  private final Map<String, SymbolInfo> symbols = new HashMap<>();

  private final Map<String, Node> values = new HashMap<>();

  private final Map<String, List<SymbolInfo>> references = new HashMap<>();

  private final Symbols parent;

  public Symbols(Symbols parent) {
    this.parent = parent;
  }

  public void define(String name, SymbolType type) {
    symbols.put(IdentifierUtil.normalize(name), new SymbolInfo(name, type));
  }

  public SymbolInfo assign(String name, Node node) {
    SymbolInfo info = symbols.get(IdentifierUtil.normalize(name));

    if (info != null) {
      return assign(info, node);
    } else if (parent != null) {
      return parent.assign(name, node);
    }

    return null;
  }

  private SymbolInfo assign(SymbolInfo info, Node node) {
    values.put(info.name(), node);

    return info;
  }

  public void reference(String name, SymbolInfo info) {
    references.compute(
        IdentifierUtil.normalize(name),
        (key, value) -> {
          SymbolInfo symbolInfo = info == null ? new SymbolInfo(name, null) : info;

          if (value == null) {
            return new ArrayList<>(List.of(symbolInfo));
          }

          value.add(info);
          return value;
        });
  }

  public SymbolInfo lookup(String name) {
    SymbolInfo info = symbols.get(IdentifierUtil.normalize(name));

    if (info != null) {
      return info;
    } else if (parent != null) {
      return parent.lookup(name);
    }

    return null; // Symbol not found in any enclosing scopes
  }
}
