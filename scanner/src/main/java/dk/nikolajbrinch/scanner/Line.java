package dk.nikolajbrinch.scanner;

/**
 * Represents a line in the source code
 *
 * @param number
 * @param content
 */
public record Line(int number, String content) {

  public Char read(int position, int column) {
    char read = content.charAt(column - 1);

    return new Char(position, this, column, read);
  }

  public boolean isEmpty(int column) {
    return (column - 1) >= content.length();
  }

  public static final Line NONE = new Line(-1, null);
}
