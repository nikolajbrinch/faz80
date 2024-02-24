package dk.nikolajbrinch.scanner;

public record Line(int number, String content) {

  public Char read(int position, int linePosition) {
    char read = content.charAt(linePosition - 1);

    return new Char(position, this, linePosition, read);
  }

  public boolean isEmpty(int linePosition) {
    return (linePosition - 1) >= content.length();
  }
}
