package dk.nikolajbrinch.faz80.parser.base;

public enum MessageType {
  ERROR("Error"),
  WARNING("Warning");

  private final String text;

  MessageType(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
