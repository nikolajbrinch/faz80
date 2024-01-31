package dk.nikolajbrinch.assembler.ide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Ide extends Application {

  private IdeController ideController;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ide.fxml"));
    VBox root = fxmlLoader.load();
    this.ideController = fxmlLoader.getController();

    size(stage);
    Scene scene = new Scene(root);
    stage.setTitle("FAZ80");
    stage.setScene(scene);
    stage.show();
  }

  private void size(Stage stage) {
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    final double width = screenBounds.getWidth() * 0.75;
    final double height = screenBounds.getHeight() * 0.75;
    stage.setWidth(width);
    stage.setHeight(height);
  }

  @Override
  public void stop() {
    ideController.dispose();
  }
}
