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

  private final NumberScanner numberScanner;

  private Mode mode = Mode.NORMAL;

  public AssemblerScanner(ScannerSource source) {
    super(source);
    this.numberScanner = new NumberScanner(this);
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
    if (mode != Mode.MACRO) {
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
    if (mode != Mode.MACRO) {
      if (checkNextChar('<')) {
        return createCharsToken(AssemblerTokenType.LESS_LESS, ch, nextChar());
      }
      if (checkNextChar('=')) {
        return createCharsToken(AssemblerTokenType.LESS_EQUAL, ch, nextChar());
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
}
