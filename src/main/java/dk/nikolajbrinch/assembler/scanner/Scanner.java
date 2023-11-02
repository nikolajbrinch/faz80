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
          token = createToken(TokenType.EOF, charReader.getLine(), charReader.getPosition());
        } else {
          Char ch = charReader.next();
          token = switch (ch.character()) {
            case '\n' -> createCharsToken(TokenType.NEWLINE, ch);
            case ';' -> createMultiCharToken(TokenType.COMMENT, ch, charReader, nextChar -> nextChar != '\n');
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
            case '&' -> {
              if (checkNextChar(charReader, '&')) {
                yield createCharsToken(TokenType.AND_AND, ch, charReader.next());
              } else if (checkNextChar(charReader, Character::isDigit)) {
                yield createHexHumberToken(ch, charReader);
              }

              yield createCharsToken(TokenType.AND, ch);
            }
            case '~' -> createCharsToken(TokenType.TILDE, ch);
            case '|' -> {
              if (checkNextChar(charReader, '|')) {
                yield createCharsToken(TokenType.PIPE_PIPE, ch, charReader.next());
              }

              yield createCharsToken(TokenType.PIPE, ch);
            }
            case '#' -> {
              if (checkNextChar(charReader, Character::isAlphabetic)) {
                yield createIdentifierToken(ch, charReader);
              }

              yield createCharsToken(TokenType.HASH, ch);
            }
            case ':' -> {
              if (checkNextChar(charReader, ':')) {
                yield createCharsToken(TokenType.COLON_COLON, ch, charReader.next());
              }

              yield createCharsToken(TokenType.COLON, ch);
            }
            case '!' -> {
              if (checkNextChar(charReader, '=')) {
                yield createCharsToken(TokenType.EQUAL_EQUAL, ch, charReader.next());
              }

              yield createCharsToken(TokenType.BANG, ch);
            }
            case '$' -> {
              if (checkNextChar(charReader, this::isHexDigit)) {
                yield createHexHumberToken(ch, charReader);
              }
              if (checkNextChar(charReader, '$')) {
                yield createCharsToken(TokenType.DOLLAR_DOLLAR, ch, charReader.next());
              }

              yield createCharsToken(TokenType.DOLLAR, ch);
            }
            case '%' -> createBinaryNumberToken(ch, charReader);
            case ',' -> createCharsToken(TokenType.COMMA, ch);
            case '"', '\'' -> createTextToken(ch, charReader);
            case '0' -> createNumberToken(ch, charReader);
            case '=' -> {
              if (checkNextChar(charReader, '=')) {
                yield createCharsToken(TokenType.EQUAL_EQUAL, ch, charReader.next());
              }

              yield createCharsToken(TokenType.EQUAL, ch);
            }
            case '<' -> {
              if (checkNextChar(charReader, '<')) {
                yield createCharsToken(TokenType.LESS_LESS, ch, charReader.next());
              }
              if (checkNextChar(charReader, '=')) {
                yield createCharsToken(TokenType.LESS_EQUAL, ch, charReader.next());
              }

              yield createCharsToken(TokenType.LESS, ch);
            }
            case '>' -> {
              if (checkNextChar(charReader, '>')) {
                Char second = charReader.next();
                if (checkNextChar(charReader, '>')) {
                  yield createCharsToken(TokenType.GREATER_GREATER_GREATER, ch, second, charReader.next());
                }
                yield createCharsToken(TokenType.GREATER_GREATER, ch, second);
              }
              if (checkNextChar(charReader, '=')) {
                yield createCharsToken(TokenType.GREATER_EQUAL, ch, charReader.next());
              }

              yield createCharsToken(TokenType.GREATER, ch);
            }
            default -> {
              if (Character.isDigit(ch.character())) {
                Token number = createMultiCharToken(TokenType.DECIMAL_NUMBER, ch, charReader, Character::isDigit);

                int value = Integer.parseInt(number.text());
                if (value >= 0 && value <= 99) {
                  if (checkNextChar(charReader, ':')) {
                    Char next = charReader.next();
                    yield new Token(TokenType.IDENTIFIER, number.line(), number.start(), next.position(),
                        number.text() + next.character());
                  } else if (checkNextChar(charReader, nextChar -> nextChar == 'b' || nextChar == 'f')) {
                    Char next = charReader.next();
                    yield new Token(TokenType.IDENTIFIER, number.line(), number.start(), next.position(),
                        number.text() + next.character());
                  } else if (checkNextChar(charReader, '$')) {
                    Char dollar = charReader.next();
                    Token identifier = new Token(TokenType.IDENTIFIER, number.line(), number.start(), dollar.position(),
                        number.text() + dollar.character());

                    if (checkNextChar(charReader, ':')) {
                      Char colon = charReader.next();
                      yield new Token(TokenType.IDENTIFIER, identifier.line(), identifier.start(), colon.position(),
                          identifier.text() + colon.character());
                    }

                    yield identifier;
                  }
                }

                yield number;
              }

              if (Character.isAlphabetic(ch.character()) || ch.character() == '_' || ch.character() == '.') {
                yield createIdentifierToken(ch, charReader);
              }

              yield null;
            }
          };
        }
      }
    } catch (IOException e) {
      throw new ScanException(e);
    }

    return token;
  }

  private Token createHexHumberToken(Char ch, CharReader charReader) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = null;

    while (checkNextChar(charReader, this::isHexDigit)) {
      last = appendChar(builder, charReader);
    }

    return new Token(TokenType.HEX_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createBinaryNumberToken(Char ch, CharReader charReader) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = null;

    while (checkNextChar(charReader, this::isBinaryDigit)) {
      last = appendChar(builder, charReader);
    }

    return new Token(TokenType.BINARY_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createOctalHumberToken(Char ch, CharReader charReader) throws IOException {
    StringBuilder builder = new StringBuilder();

    Char last = null;

    while (checkNextChar(charReader, this::isOctalDigit)) {
      last = appendChar(builder, charReader);
    }

    if (checkNextChar(charReader, ':')) {
      last = appendChar(builder, charReader);

      return new Token(TokenType.IDENTIFIER, ch.line(), ch.position(), last.position(), builder.toString());
    }

    return new Token(TokenType.OCTAL_NUMBER, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createNumberToken(Char ch, CharReader charReader) throws IOException {
    Char nextChar = charReader.peek();

    Token number = null;

    if (nextChar != null) {
      number = switch (nextChar.character()) {
        case 'x', 'X' -> {
          charReader.next();
          yield createHexHumberToken(ch, charReader);
        }
        case 'b', 'B' -> {
          Char b = charReader.next();
          if (checkNextChar(charReader, next -> !isBinaryDigit(next))) {
            yield createCharsToken(TokenType.IDENTIFIER, ch, b);
          }

          yield createBinaryNumberToken(ch, charReader);
        }
        case 'f' -> createCharsToken(TokenType.IDENTIFIER, ch, charReader.next());
        case 'o', 'O' -> {
          charReader.next();
          yield createOctalHumberToken(ch, charReader);
        }
        case '0', '1', '2', '3', '4', '5', '6', '7' -> createOctalHumberToken(ch, charReader);
        default -> {
          if (!Character.isDigit(nextChar.character())) {
            if (checkNextChar(charReader, ':')) {
              yield createCharsToken(TokenType.IDENTIFIER, ch, charReader.next());
            } else if (checkNextChar(charReader, '$')) {
              Char dollar = charReader.next();

              if (checkNextChar(charReader, ':')) {
                yield createCharsToken(TokenType.IDENTIFIER, ch, dollar, charReader.next());
              }

              yield createCharsToken(TokenType.IDENTIFIER, ch, charReader.next());
            }

            yield createCharsToken(TokenType.DECIMAL_NUMBER, ch);
          }

          yield null;
        }
      };
    }

    return number;
  }

  private Token createTextToken(Char ch, CharReader charReader) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

    Char last = ch;

    while (true) {
      Char nextChar = charReader.peek();

      if (nextChar != null) {
        if (nextChar.character() == '\\') {
          last = appendStringChar(charReader, builder);
        } else if (nextChar.character().equals(ch.character())) {
          last = appendChar(builder, charReader);
          break;
        } else {
          last = appendChar(builder, charReader);
        }
      }
    }

    TokenType type = TokenType.STRING;

    if (builder.length() == 3 && ch.character() == '\'') {
      type = TokenType.CHAR;
    }

    return new Token(type, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createIdentifierToken(Char ch, CharReader charReader) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

    Char last = ch;

    while (checkNextChar(charReader, nextChar -> Character.isLetterOrDigit(nextChar) || nextChar == ':' || nextChar == '_')) {
      last = appendChar(builder, charReader);

      if (last.character() == ':') {
        if (checkNextChar(charReader, ':')) {
          last = appendChar(builder, charReader);
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

  private CharReader.Char appendChar(StringBuilder buffer, CharReader charReader) throws IOException {
    return appendChar(buffer, charReader.next());
  }

  private CharReader.Char appendChar(StringBuilder buffer, Char ch) {
    buffer.append(ch.toString());

    return ch;
  }

  private Char appendStringChar(CharReader charReader, StringBuilder builder) throws IOException {
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

  private Token createMultiCharToken(TokenType tokenType, CharReader.Char ch, CharReader charReader, Predicate<Character> predicate)
      throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

    CharReader.Char last = ch;

    while (checkNextChar(charReader, predicate)) {
      last = appendChar(builder, charReader);
    }

    return new Token(tokenType, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private Token createToken(TokenType tokenType, int line, int position) {
    return new Token(tokenType, line, position, position, "");
  }

  private Token createCharsToken(TokenType tokenType, Char... chars) {
    Char first = chars[0];
    Char last = chars[chars.length - 1];

    return new Token(tokenType, first.line(), first.position(), last.position(),
        String.valueOf(Arrays.stream(chars).map(Char::toString).collect(Collectors.joining())));
  }

  private boolean checkNextChar(CharReader charReader, char ch) throws IOException {
    return checkNextChar(charReader, nextChar -> nextChar == ch);
  }

  private boolean checkNextChar(CharReader charReader, Predicate<Character> predicate) throws IOException {
    CharReader.Char nextChar = charReader.peek();

    return nextChar != null && predicate.test(nextChar.character());
  }

  private boolean isHexDigit(Character character) {
    return Character.isDigit(character)
        || character == 'a' || character == 'A'
        || character == 'b' || character == 'B'
        || character == 'c' || character == 'C'
        || character == 'd' || character == 'D'
        || character == 'e' || character == 'E'
        || character == 'f' || character == 'F';
  }

  private boolean isOctalDigit(Character character) {
    return character == '0' || character == '1'
        || character == '2' || character == '3'
        || character == '4' || character == '5'
        || character == '6' || character == '7';
  }

  private boolean isBinaryDigit(Character character) {
    return character == '0' || character == '1';
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
