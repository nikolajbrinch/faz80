package dk.nikolajbrinch.assembler.scanner;

import dk.nikolajbrinch.parser.BaseScanner;
import dk.nikolajbrinch.parser.Char;
import dk.nikolajbrinch.parser.Line;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AssemblerScanner extends BaseScanner<AssemblerTokenType, AssemblerToken> {
  public AssemblerScanner(InputStream inputStream) {
    super(inputStream);
  }

  @Override
  protected AssemblerToken createEofToken(Line line, int position) {
    return new AssemblerToken(AssemblerTokenType.EOF, line, position, position, "");
  }

  @Override
  protected AssemblerToken createToken() throws IOException {
    Char ch = nextChar();

    return switch (ch.character()) {
      case '\n' -> createCharsToken(AssemblerTokenType.NEWLINE, ch);
      case '\r' -> createCarriageReturnToken(ch);
      case ';' -> createCommentToken(ch);
      case '(' -> createCharsToken(AssemblerTokenType.LEFT_PAREN, ch);
      case ')' -> createCharsToken(AssemblerTokenType.RIGHT_PAREN, ch);
      case '[' -> createCharsToken(AssemblerTokenType.LEFT_BRACKET, ch);
      case ']' -> createCharsToken(AssemblerTokenType.RIGHT_BRACKET, ch);
      case '{' -> createCharsToken(AssemblerTokenType.LEFT_BRACE, ch);
      case '}' -> createCharsToken(AssemblerTokenType.RIGHT_BRACE, ch);
      case '+' -> createCharsToken(AssemblerTokenType.PLUS, ch);
      case '-' -> createCharsToken(AssemblerTokenType.MINUS, ch);
      case '*' -> createCharsToken(AssemblerTokenType.STAR, ch);
      case '/' -> createCharsToken(AssemblerTokenType.SLASH, ch);
      case '^' -> createCaretBaseToken(ch);
      case '&' -> createAmpersandBasedToken(ch);
      case '~' -> createCharsToken(AssemblerTokenType.TILDE, ch);
      case '|' -> createPipeBasedToken(ch);
      case '#' -> createHashBasedToken(ch);
      case ':' -> createColonBasedToken(ch);
      case '!' -> createBangBasedToken(ch);
      case '$' -> createDollarBasedToken(ch);
      case '%' -> createPercentBasedToken(ch);
      case ',' -> createCharsToken(AssemblerTokenType.COMMA, ch);
      case '"', '\'' -> createTextToken(ch);
      case '=' -> createEqualBasedToken(ch);
      case '<' -> createLessBasedToken(ch);
      case '>' -> createGreaterBasedToken(ch);
      default -> {
        AssemblerToken token = isNumberStart(ch.character()) ? createNumberToken(ch) : null;

        if (token != null) {
          yield token;
        }

        yield isIdentifierStart(ch.character()) ? createIdentifierToken(ch) : null;
      }
    };
  }

  @Override
  protected AssemblerToken createToken(
      AssemblerTokenType tokenType, Line line, int start, int end, String text) {
    return new AssemblerToken(tokenType, line, start, end, text);
  }

  @Override
  protected boolean isEofToken(AssemblerToken token) {
    return token.type() == AssemblerTokenType.EOF;
  }

  private AssemblerToken createCaretBaseToken(Char ch) throws IOException {
    if (checkNextChar('^')) {
      return createCharsToken(AssemblerTokenType.CARET_CARET, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.CARET, ch);
  }

  private AssemblerToken createPercentBasedToken(Char ch) throws IOException {
    if (checkNextChar(AssemblerScanner::isBinaryDigit)) {
      return createBinaryNumberToken(ch);
    }

    return createCharsToken(AssemblerTokenType.PERCENT, ch);
  }

  private AssemblerToken createCarriageReturnToken(Char ch) throws IOException {
    if (checkNextChar('\n')) {
      return createCharsToken(AssemblerTokenType.NEWLINE, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.NEWLINE, ch);
  }

  private AssemblerToken createGreaterBasedToken(Char ch) throws IOException {
    if (checkNextChar('>')) {
      Char second = nextChar();

      if (checkNextChar('>')) {
        return createCharsToken(AssemblerTokenType.GREATER_GREATER_GREATER, ch, second, nextChar());
      }

      return createCharsToken(AssemblerTokenType.GREATER_GREATER, ch, second);
    }

    if (checkNextChar('=')) {
      return createCharsToken(AssemblerTokenType.GREATER_EQUAL, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.GREATER, ch);
  }

  private AssemblerToken createLessBasedToken(Char ch) throws IOException {
    if (checkNextChar('<')) {
      return createCharsToken(AssemblerTokenType.LESS_LESS, ch, nextChar());
    }
    if (checkNextChar('=')) {
      return createCharsToken(AssemblerTokenType.LESS_EQUAL, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.LESS, ch);
  }

  private AssemblerToken createEqualBasedToken(Char ch) throws IOException {
    if (checkNextChar('=')) {
      return createCharsToken(AssemblerTokenType.EQUAL_EQUAL, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.EQUAL, ch);
  }

  private AssemblerToken createDollarBasedToken(Char ch) throws IOException {
    if (checkNextChar(AssemblerScanner::isHexDigit)) {
      return createHexHumberToken(ch);
    }

    if (checkNextChar('$')) {
      return createCharsToken(AssemblerTokenType.DOLLAR_DOLLAR, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.DOLLAR, ch);
  }

  private AssemblerToken createBangBasedToken(Char ch) throws IOException {
    if (checkNextChar('=')) {
      return createCharsToken(AssemblerTokenType.BANG_EQUAL, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.BANG, ch);
  }

  private AssemblerToken createColonBasedToken(Char ch) throws IOException {
    if (checkNextChar(':')) {
      return createCharsToken(AssemblerTokenType.COLON_COLON, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.COLON, ch);
  }

  private AssemblerToken createHashBasedToken(Char ch) throws IOException {
    if (checkNextChar(Character::isAlphabetic)) {
      return createIdentifierToken(ch);
    }

    return createCharsToken(AssemblerTokenType.HASH, ch);
  }

  private AssemblerToken createPipeBasedToken(Char ch) throws IOException {
    if (checkNextChar('|')) {
      return createCharsToken(AssemblerTokenType.PIPE_PIPE, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.PIPE, ch);
  }

  private AssemblerToken createAmpersandBasedToken(Char ch) throws IOException {
    if (checkNextChar('&')) {
      return createCharsToken(AssemblerTokenType.AND_AND, ch, nextChar());
    } else if (checkNextChar(Character::isDigit)) {
      return createHexHumberToken(ch);
    }

    return createCharsToken(AssemblerTokenType.AND, ch);
  }

  private AssemblerToken createTextToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last;

    while (true) {
      Char nextChar = peekChar();

      if (nextChar != null) {
        if (nextChar.character() == '\\') {
          last = appendStringChar(builder);
        } else if (Objects.equals(nextChar.character(), ch.character())) {
          last = appendChar(builder);
          break;
        } else {
          last = appendChar(builder);
        }
      }
    }

    AssemblerTokenType type = AssemblerTokenType.STRING;

    if (builder.length() == 3 && ch.character() == '\'') {
      type = AssemblerTokenType.CHAR;
    }

    return new AssemblerToken(type, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private AssemblerToken createIdentifierToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    while (checkNextChar(
        nextChar -> Character.isLetterOrDigit(nextChar) || nextChar == ':' || nextChar == '_')) {
      last = appendChar(builder);

      if (last.character() == ':') {
        if (checkNextChar(':')) {
          last = appendChar(builder);
        }

        break;
      }
    }

    AssemblerTokenType tokenType = AssemblerTokenType.IDENTIFIER;

    String text = builder.toString();

    if (text.equalsIgnoreCase("AF")) {
      if (checkNextChar('\'')) {
        last = appendChar(builder);
        text = builder.toString();
      }
    }

    Directive directive = Directive.find(text);

    if (directive != null) {
      tokenType = AssemblerTokenType.valueOf(directive.name());
    }

    if (tokenType == AssemblerTokenType.END) {
      tokenType = AssemblerTokenType.EOF;
    }

    return new AssemblerToken(tokenType, ch.line(), ch.position(), last.position(), text);
  }

  private AssemblerToken createCommentToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    while (checkNextChar(nextChar -> nextChar != '\n')) {
      last = appendChar(builder);
    }

    return new AssemblerToken(
        AssemblerTokenType.COMMENT, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private AssemblerToken createHexHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    Char last = ch;

    while (checkNextChar(AssemblerScanner::isHexDigit)) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.HEX_NUMBER,
            ch.line(),
            ch.position(),
            last.position(),
            builder.toString());
  }

  private AssemblerToken createBinaryNumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    Char last = ch;

    while (checkNextChar(AssemblerScanner::isBinaryDigit)) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.BINARY_NUMBER,
            ch.line(),
            ch.position(),
            last.position(),
            builder.toString());
  }

  private AssemblerToken createOctalHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    Char last = ch;

    while (checkNextChar(this::isOctalDigit)) {
      last = appendChar(builder);
    }

    if (checkNextChar(':')) {
      last = appendChar(builder);

      return new AssemblerToken(
          AssemblerTokenType.IDENTIFIER,
          ch.line(),
          ch.position(),
          last.position(),
          builder.toString());
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.OCTAL_NUMBER,
            ch.line(),
            ch.position(),
            last.position(),
            builder.toString());
  }

  private AssemblerToken createNumberToken(Char ch) throws IOException {
    AssemblerToken number = null;

    if (ch.character() == '0') {
      Char nextChar = peekChar();

      if (nextChar != null) {
        number =
            switch (nextChar.character()) {
              case 'x', 'X' -> {
                nextChar();
                yield createHexHumberToken(ch);
              }
              case 'b', 'B' -> {
                nextChar();
                yield createBinaryNumberToken(ch);
              }
              case 'o', 'O', '0', '1', '2', '3', '4', '5', '6', '7' -> {
                if (nextChar.character() == 'o' || nextChar.character() == 'O') {
                  nextChar();
                }
                yield createOctalHumberToken(ch);
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
    Char last = appendChar(builder, ch);

    while (checkNextChar(Character::isDigit)) {
      last = appendChar(builder);
    }

    if (checkNextChar(
        nextChar -> nextChar == ':' || nextChar == 'b' || nextChar == 'f' || nextChar == '$')) {
      last = appendChar(builder);

      if (last.character() == '$' && checkNextChar(':')) {
        last = appendChar(builder);
      }

      return new AssemblerToken(
          AssemblerTokenType.IDENTIFIER,
          ch.line(),
          ch.position(),
          last.position(),
          builder.toString());
    }

    return new AssemblerToken(
        AssemblerTokenType.DECIMAL_NUMBER,
        ch.line(),
        ch.position(),
        last.position(),
        builder.toString());
  }

  private Char appendStringChar(StringBuilder builder) throws IOException {
    nextChar();

    Char last = nextChar();
    switch (last.character()) {
      case 'n':
        builder.append('\n');
        break;
      case 'r':
        builder.append('\r');
        break;
      case 't':
        builder.append('\t');
        break;
      case 'f':
        builder.append('\f');
        break;
      case 'b':
        builder.append('\b');
        break;
      case '\\':
        builder.append('\\');
        break;
      case '0':
        builder.append((char) 0);
        break;
      default:
        appendChar(builder, last);
    }

    return last;
  }

  private static boolean isDecimalDigit(char character) {
    return isDigit(character, 10);
  }

  private static boolean isHexDigit(char character) {
    return isDigit(character, 16) ;
  }

  private boolean isOctalDigit(char character) {
    return isDigit(character, 8);
  }

  private static boolean isBinaryDigit(char character) {
    return isDigit(character, 2);
  }

  private static boolean isDigit(char character, int radix) {
    return Character.digit(character, radix) != -1;
  }

  private static boolean isNumberStart(char character) {
    return isDecimalDigit(character);
  }

  private static boolean isIdentifierStart(char character) {
    return Character.isAlphabetic(character)
        || character == '_'
        || character == '.'
        || character == '@';
  }
}
