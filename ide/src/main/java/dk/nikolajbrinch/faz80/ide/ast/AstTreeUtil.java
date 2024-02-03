package dk.nikolajbrinch.faz80.ide.ast;

import dk.nikolajbrinch.faz80.ide.ast.AstTreeValue.Type;
import javafx.scene.control.TreeItem;

public class AstTreeUtil {

  public static  TreeItem<AstTreeValue> searchSymbolTableNode(TreeItem<AstTreeValue> item) {
    if (item != null) {
      if (item.isLeaf()) {
        AstTreeValue treeValue = item.getValue();
        if (treeValue.type() == Type.SYMBOL_TABLE) {
          return item;
        }
      } else {
        for (TreeItem<AstTreeValue> child : item.getChildren()) {
          return searchSymbolTableNode(child);
        }
      }
    }

    return null;
  }

  public static  void expandTree(TreeItem<?> item) {
    if (item != null && !item.isLeaf()) {
      item.setExpanded(true);

      for (TreeItem<?> child : item.getChildren()) {
        expandTree(child);
      }
    }
  }
}
