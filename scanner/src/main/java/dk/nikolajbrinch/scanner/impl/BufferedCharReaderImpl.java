package dk.nikolajbrinch.scanner.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import dk.nikolajbrinch.scanner.BaseReader;
import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.CharReader;
import dk.nikolajbrinch.scanner.Line;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/** A simple Char Scanner that implements look ahead */
public class BufferedCharReaderImpl extends BaseReader<Char> implements CharReader {

  private final BufferedReader reader;
  private int lineCount = 1;

  private Line currentLine;

  private int currentPosition = 0;

  private int previousPosition = currentPosition;
  private int column = 1;

  public BufferedCharReaderImpl(InputStream inputStream) {
    this(inputStream, UTF_8);
  }

  public BufferedCharReaderImpl(InputStream inputStream, Charset charset) {
    this.reader = new BufferedReader(new InputStreamReader(inputStream, charset));
  }

  @Override
  public Line getLine() {
    return currentLine;
  }

  @Override
  public int getPosition() {
    return currentPosition;
  }

  @Override
  public int getColumn() {
    return column;
  }

  @Override
  public int getLineCount() {
    return lineCount;
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

    if (currentLine == null) {
      String string = readLine(reader);
      if (string != null) {
        currentLine = new Line(lineCount, string);
      } else {
        currentLine = null;
      }
    }

    if (currentLine != null) {
      value = currentLine.read(currentPosition, column);
      currentPosition++;

      if (currentLine.isEmpty(column + 1)) {
        lineCount++;
        column = 1;
        previousPosition = currentPosition;
        currentLine = null;
      } else {
        column++;
      }
    }

    return value;
  }

  /**
   * readLine() method that preserves EOL characters
   *
   * @return String representing a line
   * @throws IOException
   */
  private static String readLine(Reader reader) throws IOException {
    StringBuilder builder = new StringBuilder();

    int read = reader.read();

    if (read == -1) {
      return null;
    }

    char ch = (char) read;

    while (read != -1 && ch != '\r' && ch != '\n') {
      builder.append(ch);
      read = reader.read();
      ch = (char) read;
    }

    if (read != -1) {
      builder.append(ch);

      if (ch == '\r') {
        reader.mark(1);
        read = reader.read();
        ch = (char) read;

        if (ch == '\n') {
          builder.append(ch);
        } else {
          reader.reset();
        }
      }
    }

    return builder.toString();
  }

  @Override
  public void resetLine() {
    column = 1;
    currentPosition = previousPosition;
    clearBuffer();
  }

  @Override
  public void close() throws IOException {
    this.reader.close();
  }
}
