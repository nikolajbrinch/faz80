package dk.nikolajbrinch.assembler.reader;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** A simple Char Scanner that implements look ahead */
public class CharReader implements Closeable, AutoCloseable, Iterable<CharReader.Char> {

  public record Char(int line, int position, Character character) {

    @Override
    public String toString() {
      return character.toString();
    }
  }

  private final BufferedReader reader;

  private final List<Char> buffer = new LinkedList<>();

  private int line = 1;
  private int position = 1;

  public CharReader(InputStream inputStream) {
    this(inputStream, UTF_8);
  }

  public CharReader(InputStream inputStream, Charset charset) {
    this.reader = new BufferedReader(new InputStreamReader(inputStream, charset));
  }

  public int getLine() {
    return line;
  }

  public int getPosition() {
    return position;
  }

  public Char next() throws IOException {
    if (ensureBuffer(1)) {
      return buffer.removeFirst();
    }

    return null;
  }

  public boolean hasNext() throws IOException {
    if (ensureBuffer(1)) {
      return buffer.size() > 0;
    }

    return false;
  }

  /**
   * Looks at the next character
   *
   * <p>The same as <code>peek(1)</code>
   *
   * @return the next character without advancing
   * @throws IOException
   */
  public Char peek() throws IOException {
    return peek(1);
  }

  /**
   * Looks at the next characters
   *
   * @return the next n character
   * @throws IOException
   */
  public Char peek(int position) throws IOException {
    if (ensureBuffer(position)) {
      return buffer.get(position - 1);
    }

    return null;
  }

  /**
   * Fills the buffer if necessary
   *
   * @return true if the buffer is the right size, otherwise false
   * @throws IOException
   * @params size the size the buffer needs to have
   */
  private boolean ensureBuffer(int size) throws IOException {
    while (buffer.size() < size) {
      Char read = readChar();

      if (read != null) {
        buffer.add(read);
      } else {
        break;
      }
    }

    return buffer.size() >= size;
  }

  /**
   * Reads the next character from the reader
   *
   * @return a Char record describing the next character
   * @throws IOException
   */
  private Char readChar() throws IOException {
    Char value = null;

    int read = reader.read();

    if (read != -1) {
      value = new Char(line, position, (char) read);

      if (value.character == '\n') {
        line++;
        position = 1;
      } else {
        position++;
      }
    }

    return value;
  }

  @Override
  public void close() throws IOException {
    this.reader.close();
  }

  @Override
  public Iterator<Char> iterator() {

    return new Iterator<Char>() {

      @Override
      public boolean hasNext() {
        try {
          return CharReader.this.hasNext();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      @Override
      public Char next() {
        try {
          if (!CharReader.this.hasNext()) {
            throw new NoSuchElementException("No more elements");
          }

          return CharReader.this.next();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    };
  }

  public Stream<Char> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }
}
