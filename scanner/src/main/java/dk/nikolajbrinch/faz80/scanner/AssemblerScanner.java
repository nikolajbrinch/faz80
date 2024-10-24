package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.BaseScanner;
import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.CharReaderFactory;
import dk.nikolajbrinch.scanner.ErrorType;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.Position;
import dk.nikolajbrinch.scanner.ScanError;
import dk.nikolajbrinch.scanner.ScanException;
import dk.nikolajbrinch.scanner.ScannerSource;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.io.IOException;
import java.util.Objects;

public class AssemblerScanner extends BaseScanner<AssemblerTokenType, AssemblerToken> {

  private final NumberScanner numberScanner;

  private Radix radix = Radix.DECIMAL;

  private Mode mode = Mode.NORMAL;

  public AssemblerScanner(ScannerSource source) {
    super(source);
    this.numberScanner = new NumberScanner(source.getSourceInfo(), this.getCharReader(), radix);
  }

  public AssemblerScanner(ScannerSource source, CharReaderFactory factory) {
    super(source, factory);
    this.numberScanner = new NumberScanner(source.getSourceInfo(), this.getCharReader(), radix);
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  private static boolean isIdentifierStart(char character) {
    return Character.isAlphabetic(character)
        || character == '_'
        || character == '.'
        || character == '@';
  }

  @Override
  protected AssemblerToken createEofToken(
      SourceInfo sourceInfo, Position position, Line line, int column) {
    return new AssemblerToken(
        AssemblerTokenType.EOF, sourceInfo, position, line, column, column, "");
  }

  @Override
  protected AssemblerToken createToken() throws IOException {
    if (mode == Mode.MACRO_BODY) {
      return createMacroBodyToken();
    }

    Char ch = nextChar();

    AssemblerToken nextToken = null;

    try {
      nextToken =
          switch (ch.character()) {
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
            case '*' -> createStarBasedToken(ch);
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
              AssemblerToken token =
                  numberScanner.isNumberStart(ch.character())
                      ? numberScanner.createNumberToken(ch)
                      : null;

              if (token != null) {
                yield token;
              }

              yield isIdentifierStart(ch.character()) ? createIdentifierToken(ch) : null;
            }
          };
    } catch (ScanException e) {
      reportError(e);
      sync();
    }

    return nextToken;
  }

  private AssemblerToken createMacroBodyToken() throws IOException {
    Char first = peekChar();
    Char last = first;

    int index = 0;
    StringBuilder builder = new StringBuilder();

    int lastPosition = last.position();
    int lastColumn = last.column();

    while (true) {
      AssemblerToken token =
          switch (last.character()) {
            case '\n', '\\' -> createCharsToken(AssemblerTokenType.NEWLINE, nextChar());
            case '\r' -> createCarriageReturnToken(nextChar());
            case '"', '\'' -> createTextToken(nextChar());
            default -> null;
          };

      if (token != null) {
        builder.append(token.text());
        index = builder.length();
        lastPosition = getCharReader().getCurrentPosition();
        lastColumn = getCharReader().getCurrentColumn();
      } else {
        if (isEndOfBody(last)) {
          break;
        } else {
          builder.append(nextChar().character());
        }
      }

      last = peekChar();
    }

    mode = Mode.NORMAL;
    getCharReader().resetLine();

    return new AssemblerToken(
        AssemblerTokenType.TEXT,
        getSourceInfo(),
        new Position(first.position(), lastPosition),
        first.line(),
        first.column(),
        lastColumn,
        builder.substring(0, index).toString());
  }

  private boolean isEndOfBody(Char first) throws IOException {
    String[] keywords = Directive.ENDMACRO.getKeywords();

    int charLength = -1;
    for (String keyword : keywords) {
      charLength = Math.max(charLength, keyword.length());
    }

    StringBuilder builder = new StringBuilder().append(first.character());

    for (int i = 0; i < charLength; i++) {
      Char last = peekChar(i + 2);

      if (last != null) {
        builder.append(last.character());
      } else {
        throw new ScanException(
            new ScanError(
                ErrorType.UNTERMINATED_MACRO_BODY,
                createErrorToken(first, last, builder.toString())));
      }

      if (Directive.ENDMACRO.matchKeyword(builder.toString())) {
        return true;
      }
    }

    return Directive.ENDMACRO.matchKeyword(builder.toString());
  }

  private void sync() throws IOException {
    Char ch = peekChar();

    while (ch != null && (ch.character() != '\r' && ch.character() != '\n')) {
      nextChar();
      ch = peekChar();
    }
  }

  @Override
  protected boolean isEofToken(AssemblerToken token) {
    return token.type() == AssemblerTokenType.EOF;
  }

  private AssemblerToken createStarBasedToken(Char ch) throws IOException {
    if (checkNextChar('*')) {
      return createCharsToken(AssemblerTokenType.STAR_STAR, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.STAR, ch);
  }

  private AssemblerToken createCaretBaseToken(Char ch) throws IOException {
    if (checkNextChar('^')) {
      return createCharsToken(AssemblerTokenType.CARET_CARET, ch, nextChar());
    }

    return createCharsToken(AssemblerTokenType.CARET, ch);
  }

  private AssemblerToken createPercentBasedToken(Char ch) throws IOException {
    if (checkNextChar(NumberScanner::isBinaryDigit)) {
      return numberScanner.createBinaryNumberToken(ch);
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
    if (mode != Mode.MACRO_ARGUMENT) {
      if (checkNextChar('>')) {
        Char second = nextChar();

        if (checkNextChar('>')) {
          return createCharsToken(
              AssemblerTokenType.GREATER_GREATER_GREATER, ch, second, nextChar());
        }

        return createCharsToken(AssemblerTokenType.GREATER_GREATER, ch, second);
      }

      if (checkNextChar('=')) {
        return createCharsToken(AssemblerTokenType.GREATER_EQUAL, ch, nextChar());
      }
    }

    return createCharsToken(AssemblerTokenType.GREATER, ch);
  }

  private AssemblerToken createLessBasedToken(Char ch) throws IOException {
    if (mode != Mode.MACRO_ARGUMENT) {
      if (checkNextChar('<')) {
        return createCharsToken(AssemblerTokenType.LESS_LESS, ch, nextChar());
      }
      if (checkNextChar('=')) {
        return createCharsToken(AssemblerTokenType.LESS_EQUAL, ch, nextChar());
      }
      if (checkNextChar('>')) {
        return createCharsToken(AssemblerTokenType.LESS_GREATER, ch, nextChar());
      }
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
    if (checkNextChar(NumberScanner::isHexDigit)) {
      return numberScanner.createHexHumberToken(ch);
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
      return numberScanner.createHexHumberToken(ch);
    }

    return createCharsToken(AssemblerTokenType.AND, ch);
  }

  private AssemblerToken createTextToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last;

    Char prevChar = ch;

    while (true) {
      Char nextChar = peekChar();

      if (nextChar == null || nextChar.character() == '\r' || nextChar.character() == '\n') {
        throw new ScanException(
            new ScanError(
                ErrorType.UNTERMINATED_STRING, createErrorToken(ch, prevChar, builder.toString())));
      } else {
        if (nextChar.character() == '\\') {
          last = appendStringChar(builder);
        } else if (Objects.equals(nextChar.character(), ch.character())) {
          last = appendChar(builder);
          break;
        } else {
          last = appendChar(builder);
        }
      }

      prevChar = nextChar;
    }

    AssemblerTokenType type = AssemblerTokenType.STRING;

    if (builder.length() == 3 /*&& ch.character() == '\''*/) {
      type = AssemblerTokenType.CHAR;
    }

    return createToken(type, ch, last, builder.toString());
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

    return createToken(tokenType, ch, last, text);
  }

  private AssemblerToken createCommentToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder(ch.toString());
    Char last = ch;

    while (checkNextChar(nextChar -> nextChar != '\n')) {
      last = appendChar(builder);
    }

    return createToken(AssemblerTokenType.COMMENT, ch, last, builder.toString());
  }

  private Char appendStringChar(StringBuilder builder) throws IOException {
    nextChar();

    Char last = nextChar();
    switch (last.character()) {
      case 'n' -> builder.append('\n');
      case 'r' -> builder.append('\r');
      case 't' -> builder.append('\t');
      case 'f' -> builder.append('\f');
      case 'b' -> builder.append('\b');
      case '\\' -> builder.append('\\');
      case '0' -> builder.append((char) 0);
      default -> appendChar(builder, last);
    }

    return last;
  }

  private AssemblerToken createErrorToken(Char start, Char end, String text) {
    return createToken(AssemblerTokenType.ERROR, start, end, text);
  }

  private AssemblerToken createToken(AssemblerTokenType type, Char start, Char end, String text) {
    return new AssemblerToken(
        type,
        getSourceInfo(),
        new Position(start.position(), end.position()),
        start.line(),
        start.column(),
        end.column(),
        text);
  }

  @Override
  protected AssemblerToken createToken(
      AssemblerTokenType tokenType,
      SourceInfo sourceInfo,
      Position position,
      Line line,
      int startColumn,
      int endColumn,
      String text) {
    return new AssemblerToken(tokenType, sourceInfo, position, line, startColumn, endColumn, text);
  }
}
