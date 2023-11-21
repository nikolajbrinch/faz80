package dk.nikolajbrinch.assembler.scanner;

import dk.nikolajbrinch.assembler.reader.CharReader;
import dk.nikolajbrinch.assembler.reader.CharReader.Char;
import java.io.IOException;
import java.io.InputStream;

public class Scanner extends BaseScanner {

  private final NumberScanner numberScanner = new NumberScanner();

  public Scanner(InputStream inputStream) {
    super(inputStream);
  }

  @Override
  protected Token createEofToken(int line, int position) throws IOException {
    return new Token(TokenType.EOF, line, position, position, "");
  }

  @Override
  protected Token createToken() throws IOException {
    Char ch = charReader.next();

    return switch (ch.character()) {
      case '\n' -> createCharsToken(TokenType.NEWLINE, ch);
      case '\r' -> createCarriageReturnToken(ch);
      case ';' -> createCommentToken(ch);
      case '(' -> createCharsToken(TokenType.LEFT_PAREN, ch);
      case ')' -> createCharsToken(TokenType.RIGHT_PAREN, ch);
      case '[' -> createCharsToken(TokenType.LEFT_BRACKET, ch);
      case ']' -> createCharsToken(TokenType.RIGHT_BRACKET, ch);
      case '{' -> createCharsToken(TokenType.LEFT_BRACE, ch);
      case '}' -> createCharsToken(TokenType.RIGHT_BRACE, ch);
      case '+' -> createCharsToken(TokenType.PLUS, ch);
      case '-' -> createCharsToken(TokenType.MINUS, ch);
      case '*' -> createCharsToken(TokenType.STAR, ch);
      case '/' -> createCharsToken(TokenType.SLASH, ch);
      case '^' -> createCaretBaseToken(ch);
      case '&' -> createAmpersandBasedToken(ch);
      case '~' -> createCharsToken(TokenType.TILDE, ch);
      case '|' -> createPipeBasedToken(ch);
      case '#' -> createHashBasedToken(ch);
      case ':' -> createColonBasedToken(ch);
      case '!' -> createBangBasedToken(ch);
      case '$' -> createDollarBasedToken(ch);
      case '%' -> createPercentBasedToken(ch);
      case ',' -> createCharsToken(TokenType.COMMA, ch);
      case '"', '\'' -> createTextToken(ch);
      case '=' -> createEqualBasedToken(ch);
      case '<' -> createLessBasedToken(ch);
      case '>' -> createGreaterBasedToken(ch);
      default -> {
        Token token =
            numberScanner.isNumberStart(ch.character())
                ? numberScanner.createNumberToken(this, charReader, ch)
                : null;

        if (token != null) {
          yield token;
        }

        yield isIdentifierStart(ch.character()) ? createIdentifierToken(ch) : null;
      }
    };
  }

  @Override
  protected Token createToken(TokenType tokenType, int lineNumber, int start, int end, String text) {
    return new Token(tokenType, lineNumber, start, end, text);
  }

  private Token createCaretBaseToken(Char ch) throws IOException {
    if (checkNextChar('^')) {
      return createCharsToken(TokenType.CARET_CARET, ch, charReader.next());
    }

    return createCharsToken(TokenType.CARET, ch);
  }

  private Token createPercentBasedToken(Char ch) throws IOException {
    if (checkNextChar(numberScanner::isBinaryDigit)) {
      return numberScanner.createBinaryNumberToken(this, ch);
    }

    return createCharsToken(TokenType.PERCENT, ch);
  }

  private Token createCarriageReturnToken(Char ch) throws IOException {
    if (checkNextChar('\n')) {
      return createCharsToken(TokenType.NEWLINE, ch, charReader.next());
    }

    return createCharsToken(TokenType.NEWLINE, ch);
  }

  private Token createGreaterBasedToken(Char ch) throws IOException {
    if (checkNextChar('>')) {
      Char second = charReader.next();

      if (checkNextChar('>')) {
        return createCharsToken(TokenType.GREATER_GREATER_GREATER, ch, second, charReader.next());
      }

      return createCharsToken(TokenType.GREATER_GREATER, ch, second);
    }

    if (checkNextChar('=')) {
      return createCharsToken(TokenType.GREATER_EQUAL, ch, charReader.next());
    }

    return createCharsToken(TokenType.GREATER, ch);
  }

  private Token createLessBasedToken(Char ch) throws IOException {
    if (checkNextChar('<')) {
      return createCharsToken(TokenType.LESS_LESS, ch, charReader.next());
    }
    if (checkNextChar('=')) {
      return createCharsToken(TokenType.LESS_EQUAL, ch, charReader.next());
    }

    return createCharsToken(TokenType.LESS, ch);
  }

  private Token createEqualBasedToken(Char ch) throws IOException {
    if (checkNextChar('=')) {
      return createCharsToken(TokenType.EQUAL_EQUAL, ch, charReader.next());
    }

    return createCharsToken(TokenType.EQUAL, ch);
  }

  private Token createDollarBasedToken(Char ch) throws IOException {
    if (checkNextChar(numberScanner::isHexDigit)) {
      return numberScanner.createHexHumberToken(this, ch);
    }

    if (checkNextChar('$')) {
      return createCharsToken(TokenType.DOLLAR_DOLLAR, ch, charReader.next());
    }

    return createCharsToken(TokenType.DOLLAR, ch);
  }

  private Token createBangBasedToken(Char ch) throws IOException {
    if (checkNextChar('=')) {
      return createCharsToken(TokenType.EQUAL_EQUAL, ch, charReader.next());
    }

    return createCharsToken(TokenType.BANG, ch);
  }

  private Token createColonBasedToken(Char ch) throws IOException {
    if (checkNextChar(':')) {
      return createCharsToken(TokenType.COLON_COLON, ch, charReader.next());
    }

    return createCharsToken(TokenType.COLON, ch);
  }

  private Token createHashBasedToken(Char ch) throws IOException {
    if (checkNextChar(Character::isAlphabetic)) {
      return createIdentifierToken(ch);
    }

    return createCharsToken(TokenType.HASH, ch);
  }

  private Token createPipeBasedToken(Char ch) throws IOException {
    if (checkNextChar('|')) {
      return createCharsToken(TokenType.PIPE_PIPE, ch, charReader.next());
    }

    return createCharsToken(TokenType.PIPE, ch);
  }

  private Token createAmpersandBasedToken(Char ch) throws IOException {
    if (checkNextChar('&')) {
      return createCharsToken(TokenType.AND_AND, ch, charReader.next());
    } else if (checkNextChar(Character::isDigit)) {
      return numberScanner.createHexHumberToken(this, ch);
    }

    return createCharsToken(TokenType.AND, ch);
  }

  private Token createTextToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

    Char last;

    while (true) {
      Char nextChar = charReader.peek();

      if (nextChar != null) {
        if (nextChar.character() == '\\') {
          last = appendStringChar(builder);
        } else if (nextChar.character().equals(ch.character())) {
          last = appendChar(builder);
          break;
        } else {
          last = appendChar(builder);
        }
      }
    }

    TokenType type = TokenType.STRING;

    if (builder.length() == 3 && ch.character() == '\'') {
      type = TokenType.CHAR;
    }

    return new Token(type, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createIdentifierToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

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

    TokenType tokenType = TokenType.IDENTIFIER;

    String text = builder.toString();

    if (text.equalsIgnoreCase("AF")) {
      if (checkNextChar('\'')) {
        last = appendChar(builder);
        text = builder.toString();
      }
    }

    Directive directive = Directive.find(text);

    if (directive != null) {
      tokenType = TokenType.valueOf(directive.name());
    }

    if (tokenType == TokenType.END) {
      tokenType = TokenType.EOF;
    }

    return new Token(tokenType, ch.line(), ch.position(), last.position(), text);
  }

  private Token createCommentToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

    CharReader.Char last = ch;

    while (checkNextChar(nextChar -> nextChar != '\n')) {
      last = appendChar(builder);
    }

    return new Token(
        TokenType.COMMENT, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private boolean isIdentifierStart(Character character) {
    return Character.isAlphabetic(character)
        || character == '_'
        || character == '.'
        || character == '@';
  }

  private Char appendStringChar(StringBuilder builder) throws IOException {
    charReader.next();

    Char last = charReader.next();
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
