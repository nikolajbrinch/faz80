package dk.nikolajbrinch.assembler.scanner;

import dk.nikolajbrinch.parser.Token;

public record AssemblerToken(AssemblerTokenType type, int line, int start, int end, String text)
    implements Token {

  @Override
  public String toString() {
    return type() + "[@" + line() + ":" + start() + "-" + end() + "(" + sanitize(text()) + ")]";
  }

  private String sanitize(String value) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);

      builder.append(
          switch (ch) {
            case '\0' -> "\\0";
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case '\b' -> "\\b";
            case '\f' -> "\\f";
            case '\\' -> "\\";
            default -> ch;
          });
    }

    return builder.toString();
  }
}
