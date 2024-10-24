package dk.nikolajbrinch.scanner;

/**
 * Represents a position in the source code
 *
 * @param start
 * @param end
 */
public record Position(int start, int end) {

  public static final Position NONE = new Position(-1, -1);
}
