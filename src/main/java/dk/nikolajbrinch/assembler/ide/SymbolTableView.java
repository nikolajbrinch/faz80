package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.symbols.Symbol;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;

public class SymbolTableView extends TableView<SymbolProperty> {

  public SymbolTableView() {
    super();
    setStyle("-fx-font-size: 10pt;");
    getStyleClass().add("symbol-table");

    setup();
  }

  private void setup() {
    TableColumn<SymbolProperty, String> keyColumn = new TableColumn<>("Name");
    keyColumn.setCellValueFactory(cellData -> cellData.getValue().keyProperty());

    TableColumn<SymbolProperty, String> typeColumn = new TableColumn<>("Type");
    typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

    TableColumn<SymbolProperty, String> valueColumn = new TableColumn<>("Value");
    valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

    keyColumn.setMinWidth(150);
    keyColumn.setPrefWidth(150);
    keyColumn.setMaxWidth(150);

    typeColumn.setMinWidth(100);
    typeColumn.setPrefWidth(100);
    typeColumn.setMaxWidth(100);

    valueColumn.setMinWidth(500);
    valueColumn.setMaxWidth(Double.MAX_VALUE);

    getColumns().addAll(keyColumn, typeColumn, valueColumn);
  }

  public void updateProperties(TreeItem<AstTreeValue> node) {
    ObservableList<SymbolProperty> data = FXCollections.observableArrayList();

    if (node != null) {
      AstTreeValue treeValue = node.getValue();
      Object value = treeValue.value();

      SymbolTable symbolTable = null;
      if (value instanceof SymbolTable) {
        symbolTable = (SymbolTable) value;
      } else if (value instanceof String string && string.equalsIgnoreCase("symboltable")) {
        symbolTable = (SymbolTable) node.getChildren().get(0).getValue().value();
      }

      if (symbolTable != null) {
        for (Map.Entry<String, Symbol<?>> entry : symbolTable.getSymbols().entrySet()) {
          data.add(
              new SymbolProperty(
                  entry.getKey(),
                  String.valueOf(symbolTable.getSymbolType(entry.getKey())),
                  String.valueOf(entry.getValue().value())));
        }
      }
    }

    setItems(data);
  }
}
