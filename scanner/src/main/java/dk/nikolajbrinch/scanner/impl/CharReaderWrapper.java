package dk.nikolajbrinch.scanner.impl;

import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.CharReader;
import dk.nikolajbrinch.scanner.Line;
import java.io.IOException;
import java.util.Iterator;

public class CharReaderWrapper implements CharReader {

  private final CharReader charReader;

  public CharReaderWrapper(CharReader charReader) {
    this.charReader = charReader;
  }

  @Override
  public Line getLine() {
    return charReader.getLine();
  }

  @Override
  public int getPosition() {
    return charReader.getPosition();
  }

  @Override
  public int getColumn() {
    return charReader.getColumn();
  }

  @Override
  public int getLineCount() {
    return charReader.getLineCount();
  }

  @Override
  public void resetLine() {
    charReader.resetLine();
  }

  @Override
  public Char next() throws IOException {
    return charReader.next();
  }

  @Override
  public boolean hasNext() throws IOException {
    return charReader.hasNext();
  }

  @Override
  public Char peek() throws IOException {
    return charReader.peek();
  }

  @Override
  public Char peek(int position) throws IOException {
    return charReader.peek(position);
  }

  @Override
  public void close() throws IOException {
    charReader.close();
  }

  @Override
  public Iterator<Char> iterator() {
    return charReader.iterator();
  }
}
