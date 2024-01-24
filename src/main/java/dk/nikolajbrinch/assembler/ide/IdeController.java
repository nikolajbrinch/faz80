package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.AssembleException;
import dk.nikolajbrinch.assembler.compiler.Linked;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.ide.AstTreeValue.Type;
import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.ParseException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class IdeController {

  @FXML private VBox rootLayout;

  @FXML private TabPane editorTabPane;

  @FXML private TreeView<AstTreeValue> astTreeView;

  @FXML private TableView<SymbolProperty> symbolTableView;

  @FXML private TableColumn<SymbolProperty, String> name;

  @FXML private TableColumn<SymbolProperty, String> type;

  @FXML private TableColumn<SymbolProperty, String> values;

  @FXML private TabPane statusTabPane;

  @FXML private Tab outputTab;

  @FXML private Tab errorsTab;

  @FXML private TableView<ErrorProperty> errorTableView;

  @FXML private TableColumn<ErrorProperty, Integer> errorLine;

  @FXML private TableColumn<ErrorProperty, String> errorTyoe;

  @FXML private TableColumn<ErrorProperty, String> errorToken;

  @FXML private TableColumn<ErrorProperty, String> errorDescription;

  @FXML private TextArea output;

  @FXML private Button assembleButton;

  public void initialize() throws IOException {
    assembleButton.setDisable(true);
    name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    type.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    values.setCellValueFactory(cellData -> cellData.getValue().valuesProperty());

    errorLine.setCellValueFactory(cellData -> cellData.getValue().lineProperty().asObject());
    errorTyoe.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    errorToken.setCellValueFactory(cellData -> cellData.getValue().tokenProperty());
    errorDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

    errorTableView.setPlaceholder(new Label(""));
    symbolTableView.setPlaceholder(new Label(""));

    editorTabPane
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (v, oldValue, newValue) -> {
              try {
                tabSelected(newValue);
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });

    createTab("<untitled>", null);

    astTreeView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((v, oldValue, newValue) -> updateSymbols(newValue));
    astTreeView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((v, oldValue, newValue) -> treeHighlightLine(newValue));
    errorTableView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((v, oldValue, newValue) -> tableHighlightLine(newValue));
  }

  public void openFile(ActionEvent actionEvent) throws IOException {
    FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showOpenDialog(rootLayout.getScene().getWindow());

    if (file != null) {
      createTab(file.getName(), file);
    }
  }

  public void compile(ActionEvent actionEvent) throws IOException {
    TabController controller = getTabController();
    controller.compile();

    statusTabPane.getSelectionModel().select(errorsTab);

    updateErrors(controller.getErrors());

    if (!controller.hasErrors()) {
      updateOutput(controller.getLinkResult());
    }
  }

  private void updateSymbols(TreeItem<AstTreeValue> node) {
    ObservableList<SymbolProperty> data = FXCollections.observableArrayList();

    if (node != null) {
      AstTreeValue treeValue = node.getValue();

      if (treeValue.type() == Type.SYMBOL_TABLE) {
        SymbolTable symbolTable = null;
        symbolTable = (SymbolTable) treeValue.valueSupplier().get();

        if (symbolTable != null) {
          for (Map.Entry<String, List<Optional<?>>> entry : symbolTable.getSymbols().entrySet()) {
            data.add(
                new SymbolProperty(
                    entry.getKey(),
                    String.valueOf(symbolTable.getSymbolType(entry.getKey())),
                    entry.getValue().stream()
                        .map(optional -> String.valueOf(optional.orElse(null)))
                        .collect(Collectors.joining("\n"))));
          }
        }
      }
    }

    symbolTableView.setItems(data);
  }

  private void treeHighlightLine(TreeItem<AstTreeValue> node) {
    TabController controller = getTabController();
    CodeEditor editor = controller.getEditor();

    if (node != null) {
      int lineNumber = node.getValue().lineNumber();

      if (lineNumber != -1) {
        editor.highlightLine(lineNumber);
      }
    }
  }

  private void tableHighlightLine(ErrorProperty error) {
    Tab tab = editorTabPane.getSelectionModel().getSelectedItem();
    TabController controller = (TabController) tab.getUserData();
    CodeEditor editor = controller.getEditor();

    if (error != null) {
      int lineNumber = error.getLine();

      if (lineNumber != -1) {
        editor.highlightLine(lineNumber);
      }
    }
  }

  private void tabSelected(Tab tab) throws IOException {
    astTreeView.setRoot(null);
    statusTabPane.getSelectionModel().select(errorsTab);

    TabController controller = getTabController(tab);
    controller.parse();

    assembleButton.setDisable(controller.hasErrors());
    updateErrors(controller.getErrors());
  }

  private void updateErrors(List<BaseError<?>> errors) {
    ObservableList<ErrorProperty> data = FXCollections.observableArrayList();

    errors.forEach(
        error -> {
          Exception exception = error.exception();
          data.add(
              switch (exception) {
                case AssembleException assembleException ->
                    new ErrorProperty(
                        assembleException.getStatement().line().number(),
                        "Assemble",
                        "",
                        exception.getMessage());
                case ParseException parseException ->
                    new ErrorProperty(
                        parseException.getToken().line().number(),
                        "Parse",
                        parseException.getToken().text(),
                        exception.getMessage());
                default -> null;
              });
        });

    errorTableView.setItems(data);
  }

  private void updateOutput(Linked linked) {
    int address = linked.origin();

    StringBuilder builder = new StringBuilder(String.format("%04X: ", address & 0xFFFF));

    for (int i = 0; i < linked.linked().length; i++) {
      if (i > 0 && i % 16 == 0) {
        address += 16;
        builder.append("\n");
        builder.append(String.format("%04X: ", address & 0xFFFF));
      }

      builder.append(String.format("%02X", linked.linked()[i]));
      builder.append(" ");
    }

    output.setText(builder.toString());
    statusTabPane.getSelectionModel().select(outputTab);
  }

  private TabController createTab(String title, File file) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();

    Tab tab =
        fxmlLoader.load(Objects.requireNonNull(getClass().getResource("/tab.fxml")).openStream());
    TabController controller = getTabController(tab);

    tab.setText(title);
    controller.getAstTreeProperty().addListener((v, oldValue, newValue) -> treeChanged(newValue));

    if (file != null) {
      controller.setFile(file);
    }

    editorTabPane.getTabs().add(tab);
    editorTabPane.getSelectionModel().select(tab);

    return (TabController) fxmlLoader.getController();
  }

  private void treeChanged(TreeItem<AstTreeValue> astTree) {
    TabController controller = getTabController();

    Platform.runLater(
        () -> {
          astTreeView.setRoot(astTree);

          if (astTree != null) {
            expandTree(astTree);
          }

          updateErrors(controller.getErrors());
        });
  }

  private void expandTree(TreeItem<?> item) {
    if (item != null && !item.isLeaf()) {
      item.setExpanded(true);
      for (TreeItem<?> child : item.getChildren()) {
        expandTree(child);
      }
    }
  }

  private TabController getTabController() {
    return getTabController(editorTabPane.getSelectionModel().getSelectedItem());
  }

  private TabController getTabController(Tab tab) {
    return (TabController) tab.getUserData();
  }
}
