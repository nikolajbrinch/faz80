package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class AstTreeView extends TreeView<AstTreeValue> {

  public AstTreeView() {
    setStyle("-fx-font-size: 10pt;");
    setCellFactory(treeCellFactory());
    setShowRoot(true);
  }

  private static Callback<TreeView<AstTreeValue>, TreeCell<AstTreeValue>> treeCellFactory() {
    return new Callback<>() {
      @Override
      public TreeCell<AstTreeValue> call(TreeView<AstTreeValue> param) {
        return new TreeCell<>() {
          @Override
          protected void updateItem(AstTreeValue item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
              setGraphic(null);
            } else {
              if (item.value() instanceof SymbolTable) {
                setText("SymbolTable");
              } else {
                setText(item.toString());
              }
            }
          }
        };
      }
    };
  }

  public void expandTree(TreeItem<?> item) {
    if (item != null && !item.isLeaf()) {
      item.setExpanded(true);
      for (TreeItem<?> child : item.getChildren()) {
        expandTree(child);
      }
    }
  }
}
