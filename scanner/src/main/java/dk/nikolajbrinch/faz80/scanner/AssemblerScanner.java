package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.BaseScanner;
import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.Position;
import dk.nikolajbrinch.scanner.ScannerSource;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.io.IOException;
import java.util.Objects;

public class AssemblerScanner extends BaseScanner<AssemblerTokenType, AssemblerToken> {

  public AssemblerScanner(ScannerSource source) {
    super(source);
  }

  private static boolean isDecimalDigit(char character) {
    return isDigit(character, 10);
  }

  private static boolean isHexDigit(char character) {
    return isDigit(character, 16);
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

  @Override
  protected AssemblerToken createEofToken(
      SourceInfo sourceInfo, Position position, Line line, int linePosition) {
    return new AssemblerToken(
        AssemblerTokenType.EOF, sourceInfo, position, line, linePosition, linePosition, "");
  }

  @Override
  protected AssemblerToken createToken() throws IOException {
    Char ch = nextChar();

    return switch (ch.character()) {
      case '\n', '\\' -> createCharsToken(AssemblerTokenType.NEWLINE, ch);
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
      AssemblerTokenType tokenType,
      SourceInfo sourceInfo,
      Position position,
      Line line,
      int start,
      int end,
      String text) {
    return new AssemblerToken(tokenType, sourceInfo, position, line, start, end, text);
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

    if (builder.length() == 3 /*&& ch.character() == '\''*/) {
      type = AssemblerTokenType.CHAR;
    }

    return new AssemblerToken(
        type,
        getSourceInfo(),
        new Position(ch.position(), last.position()),
        ch.line(),
        ch.linePosition(),
        last.linePosition(),
        builder.toString());
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
    } else {
      if (text.startsWith("#")) {
        tokenType = AssemblerTokenType.DIRECTIVE;
      }
    }

    return new AssemblerToken(
        tokenType,
        getSourceInfo(),
        new Position(ch.position(), last.position()),
        ch.line(),
        ch.linePosition(),
        last.linePosition(),
        text);
  }

  private AssemblerToken createCommentToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    while (checkNextChar(nextChar -> nextChar != '\n')) {
      last = appendChar(builder);
    }

    return new AssemblerToken(
        AssemblerTokenType.COMMENT,
        getSourceInfo(),
        new Position(ch.position(), last.position()),
        ch.line(),
        ch.linePosition(),
        last.linePosition(),
        builder.toString());
  }

  private AssemblerToken createHexHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix = (checkNextChar('x') || checkNextChar('X') || checkNextChar('$'));

    if (prefix) {
      last = appendChar(builder);
    }

    while (checkNextChar(AssemblerScanner::isHexDigit)) {
      last = appendChar(builder);
    }

    if (!prefix && (checkNextChar('h') || checkNextChar('H'))) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.HEX_NUMBER,
            getSourceInfo(),
            new Position(ch.position(), last.position()),
            ch.line(),
            ch.linePosition(),
            last.linePosition(),
            builder.toString());
  }

  private AssemblerToken createBinaryNumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix = (checkNextChar('b') || checkNextChar('B') || checkNextChar('%'));

    if (prefix) {
      last = appendChar(builder);
    }

    while (checkNextChar(AssemblerScanner::isBinaryDigit)) {
      last = appendChar(builder);
    }

    if (!prefix && (checkNextChar('b') || checkNextChar('B'))) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.BINARY_NUMBER,
            getSourceInfo(),
            new Position(ch.position(), last.position()),
            ch.line(),
            ch.linePosition(),
            last.linePosition(),
            builder.toString());
  }

  private AssemblerToken createOctalHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    boolean prefix = (checkNextChar('o') || checkNextChar('O'));

    if (prefix) {
      last = appendChar(builder);
    }

    while (checkNextChar(this::isOctalDigit)) {
      last = appendChar(builder);
    }

    if (checkNextChar(':')) {
      last = appendChar(builder);

      return new AssemblerToken(
          AssemblerTokenType.IDENTIFIER,
          getSourceInfo(),
          new Position(ch.position(), last.position()),
          ch.line(),
          ch.linePosition(),
          last.linePosition(),
          builder.toString());
    }

    if (!prefix && (checkNextChar('o') || checkNextChar('O') || checkNextChar('q') || checkNextChar('Q'))) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.OCTAL_NUMBER,
            getSourceInfo(),
            new Position(ch.position(), last.position()),
            ch.line(),
            ch.linePosition(),
            last.linePosition(),
            builder.toString());
  }

  private AssemblerToken createDecimalNumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    while (checkNextChar(AssemblerScanner::isDecimalDigit)) {
      last = appendChar(builder);
    }

    if (checkNextChar('d') || checkNextChar('D')) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new AssemblerToken(
            AssemblerTokenType.DECIMAL_NUMBER,
            getSourceInfo(),
            new Position(ch.position(), last.position()),
            ch.line(),
            ch.linePosition(),
            last.linePosition(),
            builder.toString());
  }

  private AssemblerToken createNumberToken(Char ch) throws IOException {
    AssemblerToken number = null;

    if (ch.character() == '0') {
      Char nextChar = peekChar();

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
          getSourceInfo(),
          new Position(ch.position(), last.position()),
          ch.line(),
          ch.linePosition(),
          last.linePosition(),
          builder.toString());
    }

    return new AssemblerToken(
        AssemblerTokenType.DECIMAL_NUMBER,
        getSourceInfo(),
        new Position(ch.position(), last.position()),
        ch.line(),
        ch.linePosition(),
        last.linePosition(),
        builder.toString());
  }

  private boolean checkHexNumber() throws IOException {
    int count = 1;

    while (isHexDigit(peekChar(count).character())) {
      count++;
    }

    return count > 0 && (peekChar(count).character() == 'h' || peekChar(count).character() == 'H');
  }

  private boolean checkBinaryNumber() throws IOException {
    int count = 1;

    while (isBinaryDigit(peekChar(count).character())) {
      count++;
    }

    return count > 0 && (peekChar(count).character() == 'b' || peekChar(count).character() == 'B');
  }

  private boolean checkOctalNumber() throws IOException {
    int count = 1;

    while (isOctalDigit(peekChar(count).character())) {
      count++;
    }

    return count > 0
        && (peekChar(count).character() == 'o'
            || peekChar(count).character() == 'O'
            || peekChar(count).character() == 'q'
            || peekChar(count).character() == 'Q');
  }

  private boolean checkDecimalNumber() throws IOException {
    int count = 1;

    while (isDecimalDigit(peekChar(count).character())) {
      count++;
    }

    return count > 0 && (peekChar(count).character() == 'd' || peekChar(count).character() == 'D');
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

  private boolean isOctalDigit(char character) {
    return isDigit(character, 8);
  }
}
