package dk.nikolajbrinch.assembler.scanner;

import dk.nikolajbrinch.assembler.reader.CharReader;
import dk.nikolajbrinch.assembler.reader.CharReader.Char;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Scanner implements Iterable<Token>, AutoCloseable, Closeable {

  private final CharReader charReader;

  private final List<Token> buffer = new LinkedList<>();

  public Scanner(InputStream inputStream) {
    this.charReader = new CharReader(inputStream);
  }

  public Token next() {
    if (ensureBuffer(1)) {
      return buffer.removeFirst();
    }

    return null;
  }

  public Token peek() {
    return peek(1);
  }

  public Token peek(int position) {
    if (ensureBuffer(position)) {
      return buffer.get(position - 1);
    }

    return null;
  }

  public boolean hasNext() {
    if (ensureBuffer(1)) {
      return !buffer.isEmpty();
    }

    return false;
  }

  private boolean ensureBuffer(int size) {
    while (buffer.size() < size) {
      Token read = readToken();

      if (read != null) {
        buffer.add(read);
      } else {
        break;
      }
    }

    return buffer.size() >= size;
  }

  private Token readToken() {
    Token token = null;

    try {
      while (token == null) {
        if (!charReader.hasNext()) {
          token = createEofToken(charReader.getLine(), charReader.getPosition());
        } else {
          token = doReadToken();
        }
      }
    } catch (IOException e) {
      throw new ScanException(e);
    }

    return token;
  }

  private Token doReadToken() throws IOException {
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
      case '^' -> createCharsToken(TokenType.CARET, ch);
      case '&' -> createAmpersandBasedToken(ch);
      case '~' -> createCharsToken(TokenType.TILDE, ch);
      case '|' -> createPipeBasedToken(ch);
      case '#' -> createHashBasedToken(ch);
      case ':' -> createColonBasedToken(ch);
      case '!' -> createBangBasedToken(ch);
      case '$' -> createDollarBasedToken(ch);
      case '%' -> createBinaryNumberToken(ch);
      case ',' -> createCharsToken(TokenType.COMMA, ch);
      case '"', '\'' -> createTextToken(ch);
      case '=' -> createEqualBasedToken(ch);
      case '<' -> createLessBasedToken(ch);
      case '>' -> createGreaterBasedToken(ch);
      default -> {
        Token token = isNumberStart(ch.character()) ? createNumberToken(ch) : null;

        if (token != null) {
          yield token;
        }

        yield isIdentifierStart(ch.character()) ? createIdentifierToken(ch) : null;
      }
    };
  }

  private Token createCarriageReturnToken(Char ch) throws IOException {
    if (checkNextChar('\n')) {
      return createCharsToken(TokenType.NEWLINE, ch, charReader.next());
    }

    return createCharsToken(TokenType.NEWLINE, ch);
  }

  private Token createNumberToken(Char ch) throws IOException {
    Token number = null;

    if (ch.character() == '0') {
      Char nextChar = charReader.peek();

      if (nextChar != null) {
        number =
            switch (nextChar.character()) {
              case 'x', 'X' -> {
                charReader.next();
                yield createHexHumberToken(ch);
              }
              case 'b', 'B' -> {
                charReader.next();
                yield createBinaryNumberToken(ch);
              }
              case 'o', 'O', '0', '1', '2', '3', '4', '5', '6', '7' -> {
                if (nextChar.character() == 'o' || nextChar.character() == 'O') {
                  charReader.next();
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

      return new Token(
          TokenType.IDENTIFIER, ch.line(), ch.position(), last.position(), builder.toString());
    }

    return new Token(
        TokenType.DECIMAL_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
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
    if (checkNextChar(this::isHexDigit)) {
      return createHexHumberToken(ch);
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
      return createHexHumberToken(ch);
    }

    return createCharsToken(TokenType.AND, ch);
  }

  private Token createHexHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = ch;

    while (checkNextChar(this::isHexDigit)) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new Token(
            TokenType.HEX_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createBinaryNumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = ch;

    while (checkNextChar(this::isBinaryDigit)) {
      last = appendChar(builder);
    }

    return builder.isEmpty()
        ? null
        : new Token(
            TokenType.BINARY_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createOctalHumberToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = ch;

    while (checkNextChar(this::isOctalDigit)) {
      last = appendChar(builder);
    }

    if (checkNextChar(':')) {
      last = appendChar(builder);

      return new Token(
          TokenType.IDENTIFIER, ch.line(), ch.position(), last.position(), builder.toString());
    }

    return builder.isEmpty()
        ? null
        : new Token(
            TokenType.OCTAL_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
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

    Directive directive = Directive.find(text);

    if (directive != null) {
      tokenType = TokenType.valueOf(directive.name());
    }

    return new Token(tokenType, ch.line(), ch.position(), last.position(), text);
  }

  private CharReader.Char appendChar(StringBuilder buffer) throws IOException {
    return appendChar(buffer, charReader.next());
  }

  private CharReader.Char appendChar(StringBuilder buffer, Char ch) {
    buffer.append(ch.toString());

    return ch;
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

  private Token createEofToken(int line, int position) {
    return new Token(TokenType.EOF, line, position, position, "");
  }

  private Token createCharsToken(TokenType tokenType, Char... chars) {
    Char first = chars[0];
    Char last = chars[chars.length - 1];

    return new Token(
        tokenType,
        first.line(),
        first.position(),
        last.position(),
        String.valueOf(Arrays.stream(chars).map(Char::toString).collect(Collectors.joining())));
  }

  private boolean checkNextChar(char ch) throws IOException {
    return checkNextChar(nextChar -> nextChar == ch);
  }

  private boolean checkNextChar(Predicate<Character> predicate) throws IOException {
    CharReader.Char nextChar = charReader.peek();

    return nextChar != null && predicate.test(nextChar.character());
  }

  private boolean isDecimalDigit(Character character) {
    return Character.isDigit(character);
  }

  private boolean isHexDigit(Character character) {
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

  private boolean isBinaryDigit(Character character) {
    return character == '0' || character == '1';
  }

  private boolean isNumberStart(Character character) {
    return isDecimalDigit(character);
  }

  private boolean isIdentifierStart(Character character) {
    return Character.isAlphabetic(character) || character == '_' || character == '.';
  }

  @Override
  public Iterator<Token> iterator() {

    return new Iterator<Token>() {

      private boolean hasNext = true;

      @Override
      public boolean hasNext() {
        return hasNext;
      }

      @Override
      public Token next() {
        if (!hasNext) {
          throw new NoSuchElementException("No more elements!");
        }

        Token token = Scanner.this.next();

        if (token.type() == TokenType.EOF) {
          hasNext = false;
        }

        return token;
      }
    };
  }

  public Stream<Token> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  public void close() throws IOException {
    charReader.close();
  }
}
