package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.Position;
import java.io.IOException;

public class NumberScanner {

  private final AssemblerScanner assemblerScanner;

  public NumberScanner(AssemblerScanner assemblerScanner) {
    this.assemblerScanner = assemblerScanner;
  }

  AssemblerToken createNumberToken(Char ch) throws IOException {
    AssemblerToken number = null;

    if (ch.character() == '0') {
      Char nextChar = assemblerScanner.peekChar();

      if (nextChar != null) {
        number =
            switch (nextChar.character()) {
              case 'x', 'X' -> createHexHumberToken(ch);
              case 'b', 'B' -> {
                if (checkHexNumber()) {
                  AssemblerToken hexNumber = createHexHumberToken(ch);
                  yield hexNumber;
                }

                yield createBinaryNumberToken(ch);
              }
              case 'o', 'O', '0', '1', '2', '3', '4', '5', '6', '7' -> {
                if (checkHexNumber()) {
                  yield createHexHumberToken(ch);
                }

                if (checkBinaryNumber()) {
                  yield createBinaryNumberToken(ch);
                }

                if (checkDecimalNumber()) {
                  yield createDecimalNumberToken(ch);
                }

                yield createOctalHumberToken(ch);
              }
              default -> {
                AssemblerToken hexNumber = null;

                if (checkHexNumber()) {
                  hexNumber = createHexHumberToken(ch);
                }

                yield hexNumber;
              }
            };
      }
    }

    if (number == null) {
      if (checkHexNumber()) {
        number = createHexHumberToken(ch);
      } else if (checkBinaryNumber()) {
        number = createBinaryNumberToken(ch);
      } else if (checkOctalNumber()) {
        number = createOctalHumberToken(ch);
      } else if (checkDecimalNumber()) {
        number = createDecimalNumberToken(ch);
      }
    }

    if (number != null) {
      return number;
    }

    StringBuilder builder = new StringBuilder();
    Char last = assemblerScanner.appendChar(builder, ch);

    while (assemblerScanner.checkNextChar(Character::isDigit)) {
      last = assemblerScanner.appendChar(builder);
    }

    if (assemblerScanner.checkNextChar(
        nextChar -> nextChar == ':' || nextChar == 'b' || nextChar == 'f' || nextChar == '$')) {
      last = assemblerScanner.appendChar(builder);

      if (last.character() == '$' && assemblerScanner.checkNextChar(':')) {
        last = assemblerScanner.appendChar(builder);
      }

      return new AssemblerToken(
          AssemblerTokenType.IDENTIFIER,
          assemblerScanner.getSourceInfo(),
          new Position(ch.position(), last.position()),
          ch.line(),
          ch.linePosition(),
          last.linePosition(),
          builder.toString());
    }

    return new AssemblerToken(
        AssemblerTokenType.DECIMAL_NUMBER,
        assemblerScanner.getSourceInfo(),
        new Position(ch.position(), last.position()),
        ch.line(),
        ch.linePosition(),
        last.linePosition(),
        builder.toString());
  }

  AssemblerToken createHexHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix =
        (assemblerScanner.checkNextChar('x')
            || assemblerScanner.checkNextChar('X')
            || assemblerScanner.checkNextChar('$'));

    if (prefix) {
      last = assemblerScanner.appendChar(builder);
    }

    while (assemblerScanner.checkNextChar(NumberScanner::isHexDigit)) {
      last = assemblerScanner.appendChar(builder);
    }

    if (!prefix && (assemblerScanner.checkNextChar('h') || assemblerScanner.checkNextChar('H'))) {
      last = assemblerScanner.appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.HEX_NUMBER,
            assemblerScanner.getSourceInfo(),
            new Position(ch.position(), last.position()),
            ch.line(),
            ch.linePosition(),
            last.linePosition(),
            builder.toString());
  }

  AssemblerToken createBinaryNumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix =
        (assemblerScanner.checkNextChar('b')
            || assemblerScanner.checkNextChar('B')
            || assemblerScanner.checkNextChar('%'));

    if (prefix) {
      last = assemblerScanner.appendChar(builder);
    }

    while (assemblerScanner.checkNextChar(NumberScanner::isBinaryDigit)) {
      last = assemblerScanner.appendChar(builder);
    }

    if (!prefix && (assemblerScanner.checkNextChar('b') || assemblerScanner.checkNextChar('B'))) {
      last = assemblerScanner.appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.BINARY_NUMBER,
            assemblerScanner.getSourceInfo(),
            new Position(ch.position(), last.position()),
            ch.line(),
            ch.linePosition(),
            last.linePosition(),
            builder.toString());
  }

  private AssemblerToken createOctalHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix = (assemblerScanner.checkNextChar('o') || assemblerScanner.checkNextChar('O'));

    if (prefix) {
      last = assemblerScanner.appendChar(builder);
    }

    while (assemblerScanner.checkNextChar(NumberScanner::isOctalDigit)) {
      last = assemblerScanner.appendChar(builder);
    }

    if (assemblerScanner.checkNextChar(':')) {
      last = assemblerScanner.appendChar(builder);

      return new AssemblerToken(
          AssemblerTokenType.IDENTIFIER,
          assemblerScanner.getSourceInfo(),
          new Position(ch.position(), last.position()),
          ch.line(),
          ch.linePosition(),
          last.linePosition(),
          builder.toString());
    }

    if (!prefix
        && (assemblerScanner.checkNextChar('o')
            || assemblerScanner.checkNextChar('O')
            || assemblerScanner.checkNextChar('q')
            || assemblerScanner.checkNextChar('Q'))) {
      last = assemblerScanner.appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.OCTAL_NUMBER,
            assemblerScanner.getSourceInfo(),
            new Position(ch.position(), last.position()),
            ch.line(),
            ch.linePosition(),
            last.linePosition(),
            builder.toString());
  }

  private AssemblerToken createDecimalNumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    while (assemblerScanner.checkNextChar(NumberScanner::isDecimalDigit)) {
      last = assemblerScanner.appendChar(builder);
    }

    if (assemblerScanner.checkNextChar('d') || assemblerScanner.checkNextChar('D')) {
      last = assemblerScanner.appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.DECIMAL_NUMBER,
            assemblerScanner.getSourceInfo(),
            new Position(ch.position(), last.position()),
            ch.line(),
            ch.linePosition(),
            last.linePosition(),
            builder.toString());
  }

  private boolean checkHexNumber() throws IOException {
    int count = 1;

    while (isHexDigit(assemblerScanner.peekChar(count).character())) {
      count++;
    }

    return count > 0
        && (assemblerScanner.peekChar(count).character() == 'h'
            || assemblerScanner.peekChar(count).character() == 'H');
  }

  private boolean checkBinaryNumber() throws IOException {
    int count = 1;

    while (isBinaryDigit(assemblerScanner.peekChar(count).character())) {
      count++;
    }

    return count > 0
        && (assemblerScanner.peekChar(count).character() == 'b'
            || assemblerScanner.peekChar(count).character() == 'B');
  }

  private boolean checkOctalNumber() throws IOException {
    int count = 1;

    while (isOctalDigit(assemblerScanner.peekChar(count).character())) {
      count++;
    }

    return count > 0
        && (assemblerScanner.peekChar(count).character() == 'o'
            || assemblerScanner.peekChar(count).character() == 'O'
            || assemblerScanner.peekChar(count).character() == 'q'
            || assemblerScanner.peekChar(count).character() == 'Q');
  }

  private boolean checkDecimalNumber() throws IOException {
    int count = 1;

    while (isDecimalDigit(assemblerScanner.peekChar(count).character())) {
      count++;
    }

    return count > 0
        && (assemblerScanner.peekChar(count).character() == 'd'
            || assemblerScanner.peekChar(count).character() == 'D');
  }

  boolean isNumberStart(char character) {
    return isDecimalDigit(character);
  }

  static boolean isDecimalDigit(char character) {
    return isDigit(character, 10);
  }

  static boolean isHexDigit(char character) {
    return isDigit(character, 16);
  }

  static boolean isBinaryDigit(char character) {
    return isDigit(character, 2);
  }

  static boolean isOctalDigit(char character) {
    return isDigit(character, 8);
  }

  private static boolean isDigit(char character, int radix) {
    return Character.digit(character, radix) != -1;
  }
}
