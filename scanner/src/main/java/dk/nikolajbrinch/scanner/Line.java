package dk.nikolajbrinch.scanner;

public record Line(int number, String content) {

  public Char read(int position, int column) {
    char read = content.charAt(column - 1);

    return new Char(position, this, column, read);
  }

  public boolean isEmpty(int column) {
    return (column - 1) >= content.length();
  }
}
