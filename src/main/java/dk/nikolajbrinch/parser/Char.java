package dk.nikolajbrinch.parser;

public record Char(Line line, int position, char character) {

  @Override
  public String toString() {
    return String.valueOf(character);
  }
}
