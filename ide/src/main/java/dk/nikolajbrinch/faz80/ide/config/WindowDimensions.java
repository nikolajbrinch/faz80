package dk.nikolajbrinch.faz80.ide.config;

public class WindowDimensions {

  private double x;

  private double y;

  private double width;

  private double height;

  public WindowDimensions() {
  }

  public WindowDimensions(double x, double y, double width, double height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }
}
