package dk.nikolajbrinch.assembler.ide.symbols;

import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.ide.ast.AstTreeValue;
import dk.nikolajbrinch.assembler.ide.ast.AstTreeValue.Type;
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
