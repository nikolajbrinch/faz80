package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.AssembleError;
import dk.nikolajbrinch.assembler.compiler.AssembleException;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.symbols.Symbol;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.ide.AstTreeValue.Type;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.parser.ParseError;
import dk.nikolajbrinch.parser.ParseException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  @FXML private TableColumn<SymbolProperty, String> value;

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
    value.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

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

  public void assemble(ActionEvent actionEvent) throws IOException {
    TabController controller = getTabController();
    controller.assemble();

    statusTabPane.getSelectionModel().select(errorsTab);

    if (controller.hasParseErrors()) {
      updateParseErrors(controller.getParseErrors());
    } else {
      if (controller.hasAssembleErrors()) {
        updateAssembleErrors(controller.getAssembleErrors());
      } else {
        statusTabPane.getSelectionModel().select(outputTab);

        updateOutput(controller.getAssembleResult());
      }
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
          for (Map.Entry<String, Symbol<?>> entry : symbolTable.getSymbols().entrySet()) {
            data.add(
                new SymbolProperty(
                    entry.getKey(),
                    String.valueOf(symbolTable.getSymbolType(entry.getKey())),
                    String.valueOf(entry.getValue().value())));
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

    assembleButton.setDisable(controller.hasParseErrors());
    updateParseErrors(controller.getParseErrors());
  }

  private void updateParseErrors(List<ParseError> errors) {
    ObservableList<ErrorProperty> data = FXCollections.observableArrayList();

    errors.forEach(
        error -> {
          ParseException exception = error.exception();
          AssemblerToken token = exception.getToken();
          data.add(
              new ErrorProperty(
                  token.line().number(), "Parse", token.text(), exception.getMessage()));
        });

    errorTableView.setItems(data);
  }

  private void updateAssembleErrors(List<AssembleError> errors) {
    ObservableList<ErrorProperty> data = FXCollections.observableArrayList();

    errors.forEach(
        error -> {
          AssembleException exception = error.exception();
          Statement statement = exception.getStatement();
          data.add(
              new ErrorProperty(statement.line().number(), "Assemble", "", exception.getMessage()));
        });

    errorTableView.setItems(data);
  }

  private void updateOutput(List<ByteSource> bytes) {
    String values =
        bytes.stream()
            .flatMapToLong(source -> Arrays.stream(source.getBytes()))
            .mapToObj(value -> String.format("%02X", value & 0x00fF))
            .collect(Collectors.joining(" "));

    output.setText(values);
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
    Platform.runLater(
        () -> {
          astTreeView.setRoot(astTree);

          if (astTree != null) {
            expandTree(astTree);
          }
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
