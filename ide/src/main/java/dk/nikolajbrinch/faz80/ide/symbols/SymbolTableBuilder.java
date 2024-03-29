package dk.nikolajbrinch.faz80.ide.symbols;

import dk.nikolajbrinch.faz80.ide.ast.AstTreeUtil;
import dk.nikolajbrinch.faz80.ide.ast.AstTreeValue;
import dk.nikolajbrinch.faz80.ide.ast.AstTreeValue.Type;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolType;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class SymbolTableBuilder {

  public ObservableList<SymbolProperty> build(
      TreeItem<AstTreeValue> node, TreeItem<AstTreeValue> root) {
    ObservableList<SymbolProperty> data = FXCollections.observableArrayList();

    if (node != null) {
      AstTreeValue treeValue = node.getValue();

      SymbolTable symbolTable = null;

      TreeItem<AstTreeValue> symbolTableNode;

      if (treeValue.type() == Type.SYMBOL_TABLE) {
        symbolTableNode = node;
      } else {
        symbolTableNode = AstTreeUtil.searchSymbolTableNode(root);
      }

      if (symbolTableNode != null) {
        symbolTable = (SymbolTable) symbolTableNode.getValue().valueSupplier().get();
      }

      if (symbolTable != null) {
        for (Map.Entry<String, SymbolType> entry : symbolTable.getSymbolTypes().entrySet()) {
          data.add(
              new SymbolProperty(
                  entry.getKey(),
                  String.valueOf(entry.getValue()),
                  symbolTable.getValues(entry.getKey()).stream()
                      .map(optional -> String.valueOf(optional.orElse(null)))
                      .collect(Collectors.joining("\n"))));
        }
      }
    }

    return data;
  }
}
