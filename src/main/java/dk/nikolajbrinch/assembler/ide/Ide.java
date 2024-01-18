package dk.nikolajbrinch.assembler.ide;

import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Ide extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ide.fxml")));
    size(stage);
    Scene scene = new Scene(root);
    stage.setTitle("FZA80");
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
}
