package dk.nikolajbrinch.parser;

public record Char(int position, Line line, int linePosition, char character) {

  @Override
  public String toString() {
    return String.valueOf(character);
  }
}
