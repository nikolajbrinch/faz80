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

/**
 * A base scanner implementation.
 *
 * @param <E> The type of token type
 * @param <T> The type of token
 */
public abstract class BaseScanner<E extends TokenType, T extends Token>
    implements Scanner<T>, Iterable<T>, AutoCloseable, Closeable {

  private final SourceInfo sourceInfo;
  private final CharReader charReader;

  private final List<ScanError> errors = new ArrayList<>();

  private final List<T> buffer = new LinkedList<>();

  /**
   * Constructs a BaseScanner with the specified source.
   *
   * @param source the source to scan
   */
  protected BaseScanner(ScannerSource source) {
    this(source, s -> new BufferedCharReaderImpl(s.openStream()));
  }

  /**
   * Constructs a BaseScanner with the specified source and CharReaderFactory.
   *
   * @param source the source to scan
   * @param factory the factory to create a CharReader
   */
  protected BaseScanner(ScannerSource source, CharReaderFactory factory) {
    this.sourceInfo = source.getSourceInfo();
    try {
      this.charReader = factory.createCharReader(source);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Returns the list of scan errors.
   *
   * @return the list of scan errors
   */
  public List<ScanError> getErrors() {
    return errors;
  }

  /**
   * Returns the CharReader used by this scanner.
   *
   * @return the CharReader
   */
  protected CharReader getCharReader() {
    return charReader;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  /**
   * Returns the next token.
   *
   * @return the next token, or null if no more tokens are available
   */
  public T next() {
    if (ensureBuffer(1)) {
      return buffer.removeFirst();
    }

    return null;
  }

  /**
   * Returns the next token without removing it.
   *
   * @return the next token, or null if no more tokens are available
   */
  public T peek() {
    return peek(1);
  }

  /**
   * Returns the token at the specified position without removing it.
   *
   * @param position the position of the token to peek
   * @return the token at the specified position, or null if no more tokens are available
   */
  public T peek(int position) {
    if (ensureBuffer(position)) {
      return buffer.get(position - 1);
    }

    return null;
  }

  /**
   * Creates a new token.
   *
   * @return the created token
   * @throws IOException if an I/O error occurs
   */
  protected abstract T createToken() throws IOException;

  /**
   * Creates an EOF token.
   *
   * @param sourceInfo the source information
   * @param position the position of the EOF token
   * @param line the line of the EOF token
   * @param column the position in the line
   * @return the created EOF token
   * @throws IOException if an I/O error occurs
   */
  protected abstract T createEofToken(
      SourceInfo sourceInfo, Position position, Line line, int column) throws IOException;

  /**
   * Creates a token with the specified parameters.
   *
   * @param tokenType the type of the token
   * @param sourceInfo the source information
   * @param position the position of the token
   * @param line the line of the token
   * @param startColumn the start position of the token
   * @param endColumn the end position of the token
   * @param text the text of the token
   * @return the created token
   */
  protected abstract T createToken(
      E tokenType,
      SourceInfo sourceInfo,
      Position position,
      Line line,
      int startColumn,
      int endColumn,
      String text);

  /**
   * Reads the next character.
   *
   * @return the next character
   * @throws IOException if an I/O error occurs
   */
  protected Char nextChar() throws IOException {
    return charReader.next();
  }

  /**
   * Peeks the next character without consuming it.
   *
   * @return the next character
   * @throws IOException if an I/O error occurs
   */
  public Char peekChar() throws IOException {
    return charReader.peek();
  }

  /**
   * Peeks the character at the specified position without consuming it.
   *
   * @param position the position of the character to peek
   * @return the character at the specified position
   * @throws IOException if an I/O error occurs
   */
  public Char peekChar(int position) throws IOException {
    return charReader.peek(position);
  }

  /**
   * Appends the next character to the specified StringBuilder.
   *
   * @param buffer the StringBuilder to append to
   * @return the appended character
   * @throws IOException if an I/O error occurs
   */
  public Char appendChar(StringBuilder buffer) throws IOException {
    return appendChar(buffer, charReader.next());
  }

  /**
   * Appends the specified character to the StringBuilder.
   *
   * @param buffer the StringBuilder to append to
   * @param ch the character to append
   * @return the appended character
   */
  public Char appendChar(StringBuilder buffer, Char ch) {
    buffer.append(ch.toString());

    return ch;
  }

  /**
   * Creates a token from the specified characters.
   *
   * @param tokenType the type of the token
   * @param chars the characters of the token
   * @return the created token
   */
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

  /**
   * Checks if the next character matches the specified character.
   *
   * @param ch the character to check
   * @return true if the next character matches, false otherwise
   * @throws IOException if an I/O error occurs
   */
  public boolean checkNextChar(char ch) throws IOException {
    return checkNextChar(nextChar -> nextChar == ch);
  }

  /**
   * Checks if the next character matches the specified predicate.
   *
   * @param predicate the predicate to check
   * @return true if the next character matches, false otherwise
   * @throws IOException if an I/O error occurs
   */
  public boolean checkNextChar(Predicate<Character> predicate) throws IOException {
    Char nextChar = charReader.peek();

    return nextChar != null && predicate.test(nextChar.character());
  }

  /**
   * Returns a stream of tokens.
   *
   * @return a stream of tokens
   */
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

  /**
   * Checks if the specified token is an EOF token.
   *
   * @param token the token to check
   * @return true if the token is an EOF token, false otherwise
   */
  protected abstract boolean isEofToken(T token);

  @Override
  public void close() throws IOException {
    charReader.close();
  }

  /**
   * Ensures the buffer has at least the specified number of tokens.
   *
   * @param size the number of tokens to ensure
   * @return true if the buffer has at least the specified number of tokens, false otherwise
   */
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

  /**
   * Reads the next token.
   *
   * @return the next token, or null if no more tokens are available
   */
  private T read() {
    T token = null;

    try {
      while (token == null) {
        if (!charReader.hasNext()) {
          token =
              createEofToken(
                  sourceInfo,
                  new Position(charReader.getCurrentPosition(), charReader.getCurrentPosition()),
                  charReader.getCurrentLine() != null
                      ? charReader.getCurrentLine()
                      : new Line(charReader.getLineCount(), null),
                  charReader.getCurrentColumn());
        } else {
          token = createToken();
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return token;
  }

  /**
   * Reports a scan error.
   *
   * @param e the scan exception
   */
  protected void reportError(ScanException e) {
    errors.add(e.getError());
  }
}
