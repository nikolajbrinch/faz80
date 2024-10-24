package dk.nikolajbrinch.faz80.ide;

import dk.nikolajbrinch.faz80.ide.config.Config;
import dk.nikolajbrinch.faz80.ide.config.WindowDimensions;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Ide extends Application {

  private Stage primaryStage;

  private IdeController ideController;

  private final WindowSizeCalculator windowSizeCalculator = new WindowSizeCalculator();

  private PauseTransition resizePause;

  @Override
  public void start(Stage stage) throws Exception {
    this.primaryStage = stage;
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ide.fxml"));
    VBox root = fxmlLoader.load();
    this.ideController = fxmlLoader.getController();

    Config.INSTANCE.load();
    this.ideController.setLruFiles(Config.INSTANCE.getLruFiles());

    size(
        windowSizeCalculator.calculateWindowDimensions(
            Screen.getPrimary().getVisualBounds(), Config.INSTANCE.getWindowDimensions()));
    Scene scene = new Scene(root);
    stage.setTitle("FAZ80");
    stage.setScene(scene);
    stage.show();
  }

  private void size(WindowDimensions windowDimensions) {
    primaryStage.setX(windowDimensions.getX());
    primaryStage.setY(windowDimensions.getY());
    primaryStage.setWidth(windowDimensions.getWidth());
    primaryStage.setHeight(windowDimensions.getHeight());

    resizePause = new PauseTransition(Duration.millis(100)); // 200ms delay
    resizePause.setOnFinished(
        event ->
            Config.INSTANCE
                .setWindowDimensions(
                    new WindowDimensions(
                        primaryStage.getX(),
                        primaryStage.getY(),
                        primaryStage.getWidth(),
                        primaryStage.getHeight()))
                .save());

    primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> handleResize());
    primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> handleResize());
  }

  private void handleResize() {
    resizePause.playFromStart(); // Restart the pause timer whenever a resize happens
  }

  @Override
  public void stop() {
    ideController.dispose();
  }
}
