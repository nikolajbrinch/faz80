package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.ast.statements.BlockStatement;
import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class Ide extends Application {

  private final AstTreeView astTree = new AstTreeView();

  private final SymbolTableView symbolsTable = new SymbolTableView();

  private final CodeEditor textEditor = new CodeEditor();

  private static TreeItem<AstTreeValue> createTreeModel(BlockStatement block) {
    return new AstTreeCreator().createTree(block);
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    MenuBar menuBar = createMenu(primaryStage);

    VirtualizedScrollPane<CodeEditor> vPane = new VirtualizedScrollPane<>(textEditor);

    VBox editorContainer = new VBox();
    Label editorHeader = new Label("Source");
    editorHeader.getStyleClass().add("header-label");
    editorContainer.getChildren().addAll(editorHeader, vPane);
    VBox.setVgrow(vPane, Priority.ALWAYS);

    astTree
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((v, oldValue, newValue) -> symbolsTable.updateProperties(newValue));
    astTree
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((v, oldValue, newValue) -> highlightLine(newValue));

    VBox treeContainer = new VBox();
    Label treeHeader = new Label("AST");
    treeHeader.getStyleClass().add("header-label");
    treeContainer.getChildren().addAll(treeHeader, astTree);
    VBox.setVgrow(astTree, Priority.ALWAYS);
    treeContainer.setPrefWidth(300);

    VBox symbolsContainer = new VBox();
    Label symbolsHeader = new Label("Symbols");
    symbolsHeader.getStyleClass().add("header-label");
    symbolsContainer.getChildren().addAll(symbolsHeader, symbolsTable);
    VBox.setVgrow(symbolsTable, Priority.ALWAYS);
    symbolsContainer.setPrefWidth(600);

    SplitPane splitPane = new SplitPane();
    splitPane.getItems().addAll(editorContainer, treeContainer, symbolsContainer);

    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    final double width = screenBounds.getWidth() * 0.75;
    final double height = screenBounds.getHeight() * 0.75;
    primaryStage.setWidth(width);
    primaryStage.setHeight(height);

    Platform.runLater(
        () -> {
          menuBar.setUseSystemMenuBar(true);
          textEditor.requestFocus();
        });

    VBox rootLayout = new VBox();

    rootLayout.setFillWidth(true);

    rootLayout.getChildren().addAll(menuBar, splitPane);
    VBox.setVgrow(splitPane, Priority.ALWAYS);
    splitPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
    splitPane.setMinHeight(Region.USE_PREF_SIZE);

    rootLayout.setPrefHeight(Region.USE_COMPUTED_SIZE);
    rootLayout.setMinHeight(Region.USE_PREF_SIZE);

    Scene scene = new Scene(rootLayout);
    scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
    scene.getStylesheets().add(getClass().getResource("/styles/editor.css").toExternalForm());
    scene.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
    scene.getStylesheets().add(getClass().getResource("/styles/syntax.css").toExternalForm());

    primaryStage.setTitle("FZA80");
    primaryStage.setScene(scene);
    primaryStage.show();
    primaryStage.requestFocus();
  }

  private void highlightLine(TreeItem<AstTreeValue> node) {
    if (node != null) {
      int lineNumber = node.getValue().lineNumber();

      if (lineNumber != -1) {
        textEditor.highlightLine(lineNumber);
      }
    }
  }

  private MenuBar createMenu(Stage primaryStage) {
    MenuBar menuBar = new MenuBar();
    Menu fileMenu = new Menu("File");
    MenuItem openItem = new MenuItem("Open File");
    fileMenu.getItems().add(openItem);
    menuBar.getMenus().add(fileMenu);
    openItem.setOnAction(event -> loadFileAndDisplayContent(primaryStage));

    return menuBar;
  }

  public void loadFileAndDisplayContent(Stage primaryStage) {
    FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showOpenDialog(primaryStage);

    if (file != null) {
      loadFile(file);
    }
  }

  private void loadFile(File file) {
    try {
      String content = Files.readString(file.toPath());
      textEditor.newText(content);

      BlockStatement block = null;

      try {
        block = new AssemblerParser().parse(file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      TreeItem<AstTreeValue> rootItem = createTreeModel(block);
      astTree.setRoot(rootItem);
      astTree.expandTree(rootItem);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
