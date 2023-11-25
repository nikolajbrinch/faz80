package dk.nikolajbrinch.parser.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import dk.nikolajbrinch.parser.BaseReader;
import dk.nikolajbrinch.parser.Char;
import dk.nikolajbrinch.parser.CharReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/** A simple Char Scanner that implements look ahead */
public class CharReaderImpl extends BaseReader<Char> implements CharReader {

  private final BufferedReader reader;
  private int line = 1;
  private int position = 1;

  public CharReaderImpl(InputStream inputStream) {
    this(inputStream, UTF_8);
  }

  public CharReaderImpl(InputStream inputStream, Charset charset) {
    this.reader = new BufferedReader(new InputStreamReader(inputStream, charset));
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getPosition() {
    return position;
  }

  /**
   * Reads the next character from the reader
   *
   * @return a Char record describing the next character
   * @throws IOException
   */
  @Override
  protected Char read() throws IOException {
    Char value = null;

    int read = reader.read();

    if (read != -1) {
      value = new Char(line, position, (char) read);

      if (value.character() == '\n') {
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
}
