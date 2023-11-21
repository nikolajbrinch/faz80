package dk.nikolajbrinch.assembler.scanner;

import dk.nikolajbrinch.assembler.reader.CharReader;
import dk.nikolajbrinch.assembler.reader.CharReader.Char;
import java.io.IOException;

public class NumberScanner {
  Token createHexHumberToken(BaseScanner scanner, Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = ch;

    while (scanner.checkNextChar(this::isHexDigit)) {
      last = scanner.appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new Token(
            TokenType.HEX_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  Token createBinaryNumberToken(BaseScanner scanner, Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = ch;

    while (scanner.checkNextChar(this::isBinaryDigit)) {
      last = scanner.appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new Token(
            TokenType.BINARY_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createOctalHumberToken(BaseScanner scanner, Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = ch;

    while (scanner.checkNextChar(this::isOctalDigit)) {
      last = scanner.appendChar(builder);
    }

    if (scanner.checkNextChar(':')) {
      last = scanner.appendChar(builder);

      return new Token(
          TokenType.IDENTIFIER, ch.line(), ch.position(), last.position(), builder.toString());
    }

    return builder.isEmpty()
        ? null
        : new Token(
            TokenType.OCTAL_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  Token createNumberToken(BaseScanner scanner, CharReader charReader, Char ch) throws IOException {
    Token number = null;

    if (ch.character() == '0') {
      Char nextChar = charReader.peek();

      if (nextChar != null) {
        number =
            switch (nextChar.character()) {
              case 'x', 'X' -> {
                charReader.next();
                yield createHexHumberToken(scanner, ch);
              }
              case 'b', 'B' -> {
                charReader.next();
                yield createBinaryNumberToken(scanner, ch);
              }
              case 'o', 'O', '0', '1', '2', '3', '4', '5', '6', '7' -> {
                if (nextChar.character() == 'o' || nextChar.character() == 'O') {
                  charReader.next();
                }
                yield createOctalHumberToken(scanner, ch);
              }
              default -> {
                /*
                 * TODO: Handle that this could be a hex number, if followed by 'h' or 'H'
                 */
                yield null;
              }
            };
      }
    }

    if (number != null) {
      return number;
    }

    /*
     * Decimal number
     *
     * TODO: Handle if the string is followed by 'o', 'O', 'q', 'Q', 'd', 'D'
     */
    StringBuilder builder = new StringBuilder();
    Char last = scanner.appendChar(builder, ch);

    while (scanner.checkNextChar(Character::isDigit)) {
      last = scanner.appendChar(builder);
    }

    if (scanner.checkNextChar(
        nextChar -> nextChar == ':' || nextChar == 'b' || nextChar == 'f' || nextChar == '$')) {
      last = scanner.appendChar(builder);

      if (last.character() == '$' && scanner.checkNextChar(':')) {
        last = scanner.appendChar(builder);
      }

      return new Token(
          TokenType.IDENTIFIER, ch.line(), ch.position(), last.position(), builder.toString());
    }

    return new Token(
        TokenType.DECIMAL_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private boolean isDecimalDigit(Character character) {
    return Character.isDigit(character);
  }

  boolean isHexDigit(Character character) {
    return Character.isDigit(character)
        || character == 'a'
        || character == 'A'
        || character == 'b'
        || character == 'B'
        || character == 'c'
        || character == 'C'
        || character == 'd'
        || character == 'D'
        || character == 'e'
        || character == 'E'
        || character == 'f'
        || character == 'F';
  }

  private boolean isOctalDigit(Character character) {
    return character == '0'
        || character == '1'
        || character == '2'
        || character == '3'
        || character == '4'
        || character == '5'
        || character == '6'
        || character == '7';
  }

  boolean isBinaryDigit(Character character) {
    return character == '0' || character == '1';
  }

  boolean isNumberStart(Character character) {
    return isDecimalDigit(character);
  }
}
