package dk.nikolajbrinch.scanner.impl;

/**
 * Represents a marker in the source code
 *
 * @param start
 * @param end
 */
public record Marker(String start, String end) {

  public Marker(String start) {
    this(start, null);
  }

  public boolean startsWith(char ch) {
    return start.charAt(0) == ch;
  }
}
