package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.parser.Char;

public record Line(int number, String content) {

  public Char read(int position) {
    char read = content.charAt(position - 1);

    return new Char(this, position, read);
  }
}
