package dk.nikolajbrinch.parser;

import java.io.Closeable;
import java.io.IOException;

public interface Reader<T> extends Closeable, AutoCloseable, Iterable<T> {

  T next() throws IOException;

  boolean hasNext() throws IOException;

  T peek() throws IOException;

  T peek(int position) throws IOException;
}
