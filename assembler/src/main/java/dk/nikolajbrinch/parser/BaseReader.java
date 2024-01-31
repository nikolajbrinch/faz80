package dk.nikolajbrinch.parser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class BaseReader<T> implements Reader<T> {

  private final List<T> buffer = new LinkedList<>();

  @Override
  public T next() throws IOException {
    if (ensureBuffer(1)) {
      return buffer.removeFirst();
    }

    return null;
  }

  @Override
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
  @Override
  public T peek() throws IOException {
    return peek(1);
  }

  /**
   * Looks at the next characters
   *
   * @return the next n character
   * @throws IOException
   */
  @Override
  public T peek(int position) throws IOException {
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
  protected boolean ensureBuffer(int size) throws IOException {
    while (buffer.size() < size) {
      T read = read();

      if (read != null) {
        buffer.add(read);
      } else {
        break;
      }
    }

    return buffer.size() >= size;
  }

  protected abstract T read() throws IOException;

  @Override
  public Iterator<T> iterator() {

    return new Iterator<T>() {

      @Override
      public boolean hasNext() {
        try {
          return BaseReader.this.hasNext();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      @Override
      public T next() {
        try {
          if (!BaseReader.this.hasNext()) {
            throw new NoSuchElementException("No more elements");
          }

          return BaseReader.this.next();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    };
  }

  public Stream<T> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }
}
