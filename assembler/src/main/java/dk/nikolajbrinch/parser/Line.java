package dk.nikolajbrinch.parser;

public record Line(int number, String content) {

  public Char read(int position, int linePosition) {
    char read = content.charAt(linePosition - 1);

    return new Char(position, this, linePosition, read);
  }
}