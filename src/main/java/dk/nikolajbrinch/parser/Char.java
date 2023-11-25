package dk.nikolajbrinch.parser;

public record Char(int line, int position, Character character) {

  @Override
  public String toString() {
    return character.toString();
  }
}
