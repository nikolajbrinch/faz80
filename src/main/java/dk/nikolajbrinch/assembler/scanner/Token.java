package dk.nikolajbrinch.assembler.scanner;

public record Token(TokenType type, int line, int start, int end, String text) {

  @Override
  public String toString() {
    return  type() + "[@" + line() + ":" + start() + "-" + end() + "(" + text() + ")]";
  }
}
