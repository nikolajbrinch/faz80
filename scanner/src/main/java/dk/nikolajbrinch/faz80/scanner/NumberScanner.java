package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.CharReader;
import dk.nikolajbrinch.scanner.Position;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.io.IOException;
import java.util.function.Predicate;

/**
 * Scans numbers from the source
 */
public class NumberScanner {

  private final SourceInfo sourceInfo;
  private final CharReader charReader;

  private final Radix radix;

  public NumberScanner(SourceInfo sourceInfo, CharReader charReader, Radix radix) {
    this.sourceInfo = sourceInfo;
    this.charReader = charReader;
    this.radix = radix;
  }

  AssemblerToken createNumberToken(Char ch) throws IOException {
    AssemblerToken number = null;

    if (ch.character() == '0') {
      Char nextChar = peekChar();

      if (nextChar != null) {
        number =
            switch (nextChar.character()) {
              case 'x', 'X' -> createHexHumberToken(ch);
              case 'b', 'B' -> tryBinaryNumber(ch);
              case 'o', 'O', '0', '1', '2', '3', '4', '5', '6', '7' -> tryOctalNUmber(ch);
              default -> tryHexNumber(ch);
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
    Char last = appendChar(builder, ch);

    while (matchChar(Character::isDigit)) {
      last = appendChar(builder);
    }

    if (isIdentifier()) {
      last = appendChar(builder);

      if (isColonOrDollar()) {
        last = appendChar(builder);
      }

      return createToken(AssemblerTokenType.IDENTIFIER, ch, last, builder.toString());
    }

    return createToken(AssemblerTokenType.DECIMAL_NUMBER, ch, last, builder.toString());
  }

  private AssemblerToken tryHexNumber(Char ch) throws IOException {
    AssemblerToken hexNumber = null;

    if (checkHexNumber()) {
      hexNumber = createHexHumberToken(ch);
    }

    return hexNumber;
  }

  private AssemblerToken tryOctalNUmber(Char ch) throws IOException {
    if (checkHexNumber()) {
      return createHexHumberToken(ch);
    }

    if (checkBinaryNumber()) {
      return createBinaryNumberToken(ch);
    }

    if (checkDecimalNumber()) {
      return createDecimalNumberToken(ch);
    }

    return createOctalHumberToken(ch);
  }

  private AssemblerToken tryBinaryNumber(Char ch) throws IOException {
    if (checkHexNumber()) {
      return createHexHumberToken(ch);
    }

    return createBinaryNumberToken(ch);
  }

  AssemblerToken createHexHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix = matchChar('x', 'X', '$');

    if (prefix) {
      last = appendChar(builder);
    }

    while (matchChar(NumberScanner::isHexDigit)) {
      last = appendChar(builder);
    }

    if (!prefix && matchChar('h', 'H')) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : createToken(AssemblerTokenType.HEX_NUMBER, ch, last, builder.toString());
  }

  AssemblerToken createBinaryNumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix = matchChar('b', 'B', '%');

    if (prefix) {
      last = appendChar(builder);
    }

    while (matchChar(NumberScanner::isBinaryDigit)) {
      last = appendChar(builder);
    }

    if (!prefix && (matchChar('b', 'B'))) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : createToken(AssemblerTokenType.BINARY_NUMBER, ch, last, builder.toString());
  }

  private AssemblerToken createOctalHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix = matchChar('o', 'O');

    if (prefix) {
      last = appendChar(builder);
    }

    while (matchChar(NumberScanner::isOctalDigit)) {
      last = appendChar(builder);
    }

    if (matchChar(':')) {
      last = appendChar(builder);

      return createToken(AssemblerTokenType.IDENTIFIER, ch, last, builder.toString());
    }

    if (!prefix && matchChar('o', 'O', 'q', 'Q')) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : createToken(AssemblerTokenType.OCTAL_NUMBER, ch, last, builder.toString());
  }

  private AssemblerToken createDecimalNumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    while (matchChar(NumberScanner::isDecimalDigit)) {
      last = appendChar(builder);
    }

    if (matchChar('d', 'D')) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : createToken(AssemblerTokenType.DECIMAL_NUMBER, ch, last, builder.toString());
  }

  private boolean checkHexNumber() throws IOException {
    int count = 1;

    while (peekChar(count) != null && isHexDigit(peekChar(count).character())) {
      count++;
    }

    return count > 0 && matchChar(peekChar(count), 'h', 'H');
  }

  private boolean checkBinaryNumber() throws IOException {
    int count = 1;

    while (peekChar(count) != null && isBinaryDigit(peekChar(count).character())) {
      count++;
    }

    return count > 0 && matchChar(peekChar(count), 'b', 'B');
  }

  private boolean checkOctalNumber() throws IOException {
    int count = 1;

    while (peekChar(count) != null && isOctalDigit(peekChar(count).character())) {
      count++;
    }

    return count > 0 && matchChar(peekChar(count), 'o', 'O', 'q', 'Q');
  }

  private boolean checkDecimalNumber() throws IOException {
    int count = 1;

    while (peekChar(count) != null && isDecimalDigit(peekChar(count).character())) {
      count++;
    }

    return count > 0 && matchChar(peekChar(count), 'd', 'D');
  }

  boolean isNumberStart(char character) {
    return isDecimalDigit(character);
  }

  private boolean isIdentifier() throws IOException {
    return isColonOrDollar() || matchChar('b', 'f');
  }

  private boolean isColonOrDollar() throws IOException {
    return matchChar(':', '$');
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

  private boolean matchChar(char... chars) throws IOException {
    return matchChar(peekChar(), chars);
  }

  private boolean matchChar(Char character, char... chars) {
    for (char ch : chars) {
      if (checkChar(character, ch)) {
        return true;
      }
    }

    return false;
  }

  public boolean matchChar(Predicate<Character> predicate) throws IOException {
    return matchChar(peekChar(), predicate);
  }

  private boolean matchChar(Char character, Predicate<Character> predicate) {
    return checkChar(character, predicate);
  }

  private boolean checkChar(Char character, char ch) {
    if (character == null) {
      return false;
    }

    return character.character() == ch;
  }

  private boolean checkChar(Char character, Predicate<Character> predicate) {
    if (character == null) {
      return false;
    }

    return predicate.test(character.character());
  }

  private Char peekChar() throws IOException {
    return charReader.peek();
  }

  private Char peekChar(int position) throws IOException {
    return charReader.peek(position);
  }

  private Char appendChar(StringBuilder buffer) throws IOException {
    return appendChar(buffer, charReader.next());
  }

  private Char appendChar(StringBuilder buffer, Char ch) {
    buffer.append(ch.toString());

    return ch;
  }

  private AssemblerToken createToken(AssemblerTokenType type, Char start, Char end, String text) {
    return new AssemblerToken(
        type,
        sourceInfo,
        new Position(start.position(), end.position()),
        start.line(),
        start.column(),
        end.column(),
        text);
  }
}
