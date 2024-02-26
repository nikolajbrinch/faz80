package dk.nikolajbrinch.scanner;

public record Char(int position, Line line, int column, char character) {

  @Override
  public String toString() {
    return String.valueOf(character);
  }
}
