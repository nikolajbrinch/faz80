package dk.nikolajbrinch.scanner;

/**
 * Represents a character read from the input along with its position and column information.
 *
 * @param position the position of the character in the input
 * @param line the line of the character
 * @param column the column of the character in the current line
 * @param character the character read
 */
public record Char(int position, Line line, int column, char character) {

  @Override
  public String toString() {
    return String.valueOf(character);
  }
}
