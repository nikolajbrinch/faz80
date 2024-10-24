package dk.nikolajbrinch.faz80.ide;

import dk.nikolajbrinch.faz80.ide.config.WindowDimensions;
import javafx.geometry.Rectangle2D;

public class WindowSizeCalculator {

  public WindowDimensions calculateWindowDimensions(
      final Rectangle2D visualBounds, final WindowDimensions windowDimensions) {
    WindowDimensions dimensions;

    final double visualBoundsWidth = visualBounds.getWidth();
    final double visualBoundsHeight = visualBounds.getHeight();

    if (windowDimensions != null) {
      double x = Math.max(0, windowDimensions.getX());
      double y = Math.max(0, windowDimensions.getY());
      Rectangle2D windowBounds =
          new Rectangle2D(x, y, windowDimensions.getWidth(), windowDimensions.getHeight());
      double width = Math.min(windowBounds.getWidth(), visualBoundsWidth);
      double height = Math.min(windowBounds.getHeight(), visualBoundsHeight);

      dimensions = new WindowDimensions(x, y, width, height);
    } else {
      dimensions = calculateDefaultWindowDimensions(visualBoundsWidth, visualBoundsHeight);
    }
    return dimensions;
  }

  private WindowDimensions calculateDefaultWindowDimensions(
      double visualBoundsWidth, double visualBoundsHeight) {
    final double width = visualBoundsWidth * 0.75;
    final double height = visualBoundsHeight * 0.75;
    final double x = (visualBoundsWidth - width) / 2;
    final double y = (visualBoundsHeight - height) / 2;

    return new WindowDimensions(x, y, width, height);
  }
}
