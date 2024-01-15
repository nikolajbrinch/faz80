package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import javafx.scene.control.TreeItem;

public class SymbolTableTreeItem extends TreeItem<AstTreeValue> {

  public SymbolTableTreeItem(SymbolTable symbolTable) {
    super(new AstTreeValue(-1, symbolTable));
  }

  @Override
  public String toString() {
    return "SymbolTable";
  }
}
