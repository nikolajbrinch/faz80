package dk.nikolajbrinch.faz80.ide;

import dk.nikolajbrinch.faz80.assembler.AssembleResult;
import dk.nikolajbrinch.faz80.ide.ast.AstTreeUtil;
import dk.nikolajbrinch.faz80.ide.ast.AstTreeValue;
import dk.nikolajbrinch.faz80.ide.editor.EditorTabController;
import dk.nikolajbrinch.faz80.ide.format.Formatter;
import dk.nikolajbrinch.faz80.ide.symbols.SymbolProperty;
import dk.nikolajbrinch.faz80.ide.symbols.SymbolTableBuilder;
import dk.nikolajbrinch.faz80.linker.LinkResult;
import dk.nikolajbrinch.faz80.base.errors.BaseError;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
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

  private final OutputBuilder outputBuilder = new OutputBuilder();
  private final ListingBuilder listingBuilder = new ListingBuilder();
  private final ErrorListBuilder errorListBuilder = new ErrorListBuilder();
  private final SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder();
  private final Formatter formatter = new Formatter();
  private final TaskManager taskManager = new TaskManager();

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

  @FXML private TableColumn<ErrorProperty, String> errorLocation;

  @FXML private TableColumn<ErrorProperty, String> errorType;

  @FXML private TableColumn<ErrorProperty, Integer> errorLine;

  @FXML private TableColumn<ErrorProperty, String> errorTask;

  @FXML private TableColumn<ErrorProperty, String> errorToken;

  @FXML private TableColumn<ErrorProperty, String> errorDescription;

  @FXML private TextArea output;

  @FXML private TextArea listing;

  @FXML private Button assembleButton;

  private int untitledCounter = 1;

  public void initialize() throws IOException {
    assembleButton.setDisable(true);
    setupSymbolTable();
    setupErrorsTab();

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

    createTab();

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

  public void newFile(ActionEvent actionEvent) throws IOException {
    createTab();
  }

  public void openFile(ActionEvent actionEvent) throws IOException {
    FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showOpenDialog(rootLayout.getScene().getWindow());

    if (file != null) {
      createTab(file.getName(), file);
    }
  }

  public void closeFile(ActionEvent actionEvent) throws IOException {
    getTabController().dispose();
    Tab tab = editorTabPane.getSelectionModel().getSelectedItem();
    editorTabPane.getTabs().remove(tab);
  }

  public void compile(ActionEvent actionEvent) throws IOException {
    EditorTabController controller = getTabController();
    controller.compile();

    statusTabPane.getSelectionModel().select(errorsTab);

    updateErrors(controller.getErrors());

    if (!controller.hasErrors()) {
      updateOutput(controller.getLinkResult());
      updateListing(controller.getAssembleResult());
    }
  }

  public void format(ActionEvent actionEvent) throws IOException {
    EditorTabController controller = getTabController();
    String text = controller.getEditor().getText();
    String formattedText = formatter.format(controller.getTabStop(), text);
    Platform.runLater(
        () -> getTabController().getEditor().replaceText(formattedText));
  }

  private void updateSymbols(TreeItem<AstTreeValue> node) {
    Platform.runLater(
        () -> symbolTableView.setItems(symbolTableBuilder.build(node, astTreeView.getRoot())));
  }

  private void treeHighlightLine(TreeItem<AstTreeValue> node) {
    if (node != null) {
      getTabController().highlightLine(node.getValue().lineNumber());
    }
  }

  private void tableHighlightLine(ErrorProperty error) {
    if (error != null) {
      getTabController().highlightLine(error.getLine());
    }
  }

  private void tabSelected(Tab tab) throws IOException {
    if (tab == null) {
      return;
    }

    astTreeView.setRoot(null);
    statusTabPane.getSelectionModel().select(errorsTab);
    EditorTabController controller = getTabController(tab);
    controller.parse();

    assembleButton.setDisable(controller.hasErrors());
    updateErrors(controller.getErrors());
    updateOutput(controller.getLinkResult());
    updateListing(controller.getAssembleResult());
  }

  private void updateErrors(List<BaseError<?>> errors) {
    ObservableList<ErrorProperty> data = errorListBuilder.build(errors, getFile());

    Platform.runLater(
        () -> {
          errorTableView.setItems(data);

          if (!data.isEmpty()) {
            statusTabPane.getSelectionModel().select(errorsTab);
          }
        });
  }

  private void updateOutput(LinkResult linkResult) {
    if (linkResult != null) {
      Platform.runLater(
          () -> {
            output.setText(outputBuilder.build(linkResult.linked()));
            statusTabPane.getSelectionModel().select(outputTab);
          });
    }
  }

  private void updateListing(AssembleResult assembleResult) {
    if (assembleResult != null) {
      Platform.runLater(() -> listing.setText(listingBuilder.build(assembleResult.assembled())));
    }
  }

  private EditorTabController createTab() throws IOException {
    String title = String.format("<untitled-%d>", untitledCounter);
    untitledCounter++;
    return createTab(title, null);
  }

  private EditorTabController createTab(String title, File file) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();

    Tab tab =
        fxmlLoader.load(
            Objects.requireNonNull(getClass().getResource("/editor-tab.fxml")).openStream());
    EditorTabController controller = getTabController(tab);

    tab.setText(title);
    controller.astTreeProperty().addListener((v, oldValue, newValue) -> treeChanged(newValue));
    controller.errorsProperty().addListener((v, oldValue, newValue) -> updateErrors(newValue));

    if (file != null) {
      controller.setFile(file);
    }

    editorTabPane.getTabs().add(tab);
    editorTabPane.getSelectionModel().select(tab);

    return fxmlLoader.getController();
  }

  private void treeChanged(TreeItem<AstTreeValue> astTree) {
    Platform.runLater(
        () -> {
          astTreeView.setRoot(astTree);
          AstTreeUtil.expandTree(astTree);
          astTreeView.getSelectionModel().select(AstTreeUtil.searchSymbolTableNode(astTree));
        });
  }

  private File getFile() {
    return new File(getTabController().getSource().getSourceInfo().name());
  }

  private EditorTabController getTabController() {
    return getTabController(editorTabPane.getSelectionModel().getSelectedItem());
  }

  private EditorTabController getTabController(Tab tab) {
    return (EditorTabController) tab.getUserData();
  }

  private void setupSymbolTable() {
    name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    type.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    values.setCellValueFactory(cellData -> cellData.getValue().valuesProperty());
    symbolTableView.setPlaceholder(new Label(""));
  }

  private void setupErrorsTab() {
    errorLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
    errorType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    errorLine.setCellValueFactory(cellData -> cellData.getValue().lineProperty().asObject());
    errorTask.setCellValueFactory(cellData -> cellData.getValue().taskProperty());
    errorToken.setCellValueFactory(cellData -> cellData.getValue().tokenProperty());
    errorDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
    errorTableView.setPlaceholder(new Label(""));
  }

  public void dispose() {
    taskManager.shutdown();
  }
}
