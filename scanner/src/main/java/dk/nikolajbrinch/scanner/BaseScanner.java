package dk.nikolajbrinch.scanner;

import dk.nikolajbrinch.scanner.impl.BufferedCharReaderImpl;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class BaseScanner<E extends TokenType, T extends Token>
    implements Scanner<T>, Iterable<T>, AutoCloseable, Closeable {

  private final SourceInfo sourceInfo;
  private final CharReader charReader;

  private final List<ScanError> errors = new ArrayList<>();

  private final List<T> buffer = new LinkedList<>();

  public BaseScanner(ScannerSource source) {
    this.sourceInfo = source.getSourceInfo();
    try {
      this.charReader = new BufferedCharReaderImpl(source.openStream());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public List<ScanError> getErrors() {
    return errors;
  }

  protected CharReader getCharReader() {
    return charReader;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  public T next() {
    if (ensureBuffer(1)) {
      return buffer.removeFirst();
    }

    return null;
  }

  public T peek() {
    return peek(1);
  }

  public T peek(int position) {
    if (ensureBuffer(position)) {
      return buffer.get(position - 1);
    }

    return null;
  }

  protected abstract T createToken() throws IOException;

  protected abstract T createEofToken(
      SourceInfo sourceInfo, Position position, Line line, int linePosition) throws IOException;

  protected abstract T createToken(
      E tokenType,
      SourceInfo sourceInfo,
      Position position,
      Line line,
      int start,
      int end,
      String text);

  protected Char nextChar() throws IOException {
    return charReader.next();
  }

  public Char peekChar() throws IOException {
    return charReader.peek();
  }

  public Char peekChar(int position) throws IOException {
    return charReader.peek(position);
  }

  public Char appendChar(StringBuilder buffer) throws IOException {
    return appendChar(buffer, charReader.next());
  }

  public Char appendChar(StringBuilder buffer, Char ch) {
    buffer.append(ch.toString());

    return ch;
  }

  protected T createCharsToken(E tokenType, Char... chars) {
    Char first = chars[0];
    Char last = chars[chars.length - 1];

    char[] charArray = new char[chars.length];
    for (int i = 0; i < chars.length; i++) {
      charArray[i] = chars[i].character();
    }

    return createToken(
        tokenType,
        sourceInfo,
        new Position(first.position(), last.position()),
        first.line(),
        first.column(),
        last.column(),
        new String(charArray));
  }

  public boolean checkNextChar(char ch) throws IOException {
    return checkNextChar(nextChar -> nextChar == ch);
  }

  public boolean checkNextChar(Predicate<Character> predicate) throws IOException {
    Char nextChar = charReader.peek();

    return nextChar != null && predicate.test(nextChar.character());
  }

  public Stream<T> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  public Iterator<T> iterator() {

    return new Iterator<T>() {

      private boolean hasNext = true;

      @Override
      public boolean hasNext() {
        return hasNext;
      }

      @Override
      public T next() {
        if (!hasNext) {
          throw new NoSuchElementException("No more elements!");
        }

        T token = BaseScanner.this.next();

        if (isEofToken(token)) {
          hasNext = false;
        }

        return token;
      }
    };
  }

  protected abstract boolean isEofToken(T token);

  @Override
  public void close() throws IOException {
    charReader.close();
  }

  private boolean ensureBuffer(int size) {
    while (buffer.size() < size) {
      T read = read();

      if (null != read) {
        buffer.add(read);
      } else {
        break;
      }
    }

    return buffer.size() >= size;
  }

  private T read() {
    T token = null;

    try {
      while (token == null) {
        if (!charReader.hasNext()) {
          token =
              createEofToken(
                  sourceInfo,
                  new Position(charReader.getPosition(), charReader.getPosition()),
                  charReader.getLine() != null
                      ? charReader.getLine()
                      : new Line(charReader.getLineCount(), null),
                  charReader.getColumn());
        } else {
          token = createToken();
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return token;
  }

  protected void reportError(ScanException e) {
    errors.add(e.getError());
  }
}
