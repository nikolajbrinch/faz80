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

public abstract class BaseScanner implements Iterable<Token>, AutoCloseable, Closeable {

  final CharReader charReader;

  private final List<Token> buffer = new LinkedList<>();

  public BaseScanner(InputStream inputStream) {
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

  protected abstract Token createToken() throws IOException;

  protected abstract Token createEofToken(int line, int position) throws IOException;

  protected abstract Token createToken(TokenType tokenType, int lineNumber, int start, int end, String text);

  protected Char appendChar(StringBuilder buffer) throws IOException {
    return appendChar(buffer, charReader.next());
  }

  protected Char appendChar(StringBuilder buffer, Char ch) {
    buffer.append(ch.toString());

    return ch;
  }

  protected Token createCharsToken(TokenType tokenType, Char... chars) {
    Char first = chars[0];
    Char last = chars[chars.length - 1];

    return createToken(
        tokenType,
        first.line(),
        first.position(),
        last.position(),
        String.valueOf(Arrays.stream(chars).map(Char::toString).collect(Collectors.joining())));
  }


  protected boolean checkNextChar(char ch) throws IOException {
    return checkNextChar(nextChar -> nextChar == ch);
  }

  protected boolean checkNextChar(Predicate<Character> predicate) throws IOException {
    Char nextChar = charReader.peek();

    return nextChar != null && predicate.test(nextChar.character());
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

        Token token = BaseScanner.this.next();

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
          token = createToken();
        }
      }
    } catch (IOException e) {
      throw new ScanException(e);
    }

    return token;
  }
}
