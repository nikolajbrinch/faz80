package dk.nikolajbrinch.faz80.ide.symbols;

import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;
import dk.nikolajbrinch.faz80.ide.ast.AstTreeValue;
import dk.nikolajbrinch.faz80.ide.ast.AstTreeValue.Type;
import javafx.scene.control.TreeItem;

public class SymbolTableTreeItem extends TreeItem<AstTreeValue> {

  public SymbolTableTreeItem(SymbolTable symbolTable) {
    super(new AstTreeValue(-1, Type.SYMBOL_TABLE, () -> symbolTable));
  }

  @Override
  public String toString() {
    return "SymbolTable";
  }
}
