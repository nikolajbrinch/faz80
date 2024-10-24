package dk.nikolajbrinch.scanner.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import dk.nikolajbrinch.scanner.BaseReader;
import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.CharReader;
import dk.nikolajbrinch.scanner.Line;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * A simple Char Scanner that implements look ahead. This class reads characters from an input
 * stream or string, providing look-ahead capabilities. It maintains the current line, position, and
 * column information to facilitate character reading.
 */
public class BufferedCharReaderImpl extends BaseReader<Char> implements CharReader {

  private final BufferedReader reader;
  private int lineCount = 1;

  private Line currentLine;

  private int currentPosition = 0;

  private int previousPosition = currentPosition;
  private int column = 1;

  /**
   * Constructs a BufferedCharReaderImpl with the specified input stream using UTF-8 encoding.
   *
   * @param inputStream the input stream to read from
   */
  public BufferedCharReaderImpl(InputStream inputStream) {
    this(inputStream, UTF_8);
  }

  /**
   * Constructs a BufferedCharReaderImpl with the specified input stream and charset.
   *
   * @param inputStream the input stream to read from
   * @param charset the charset to use for decoding
   */
  public BufferedCharReaderImpl(InputStream inputStream, Charset charset) {
    this.reader = new BufferedReader(new InputStreamReader(inputStream, charset));
  }

  /**
   * Constructs a BufferedCharReaderImpl with the specified string source using UTF-8 encoding.
   *
   * @param source the string source to read from
   */
  public BufferedCharReaderImpl(String source) {
    this(source, UTF_8);
  }

  /**
   * Constructs a BufferedCharReaderImpl with the specified string source and charset.
   *
   * @param source the string source to read from
   * @param charset the charset to use for decoding
   */
  public BufferedCharReaderImpl(String source, Charset charset) {
    this(new ByteArrayInputStream(source.getBytes(charset)), charset);
  }

  /**
   * Returns the current line being read.
   *
   * @return the current line
   */
  @Override
  public Line getCurrentLine() {
    return currentLine;
  }

  /**
   * Returns the current position in the input.
   *
   * @return the current position
   */
  @Override
  public int getCurrentPosition() {
    return currentPosition;
  }

  /**
   * Returns the current column in the line.
   *
   * @return the current column
   */
  @Override
  public int getCurrentColumn() {
    return column;
  }

  /**
   * Returns the number of lines read.
   *
   * @return the number of lines read
   */
  @Override
  public int getLineCount() {
    return lineCount;
  }

  /**
   * Reads the next character from the reader.
   *
   * @return a Char record describing the next character
   * @throws IOException if an I/O error occurs
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
   * Reads a line from the reader, preserving EOL characters.
   *
   * @param reader the reader to read from
   * @return a String representing a line
   * @throws IOException if an I/O error occurs
   */
  protected String readLine(Reader reader) throws IOException {
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

  /** Resets the line to the previous position. */
  @Override
  public void resetLine() {
    column = 1;
    currentPosition = previousPosition;
    clearBuffer();
  }

  /**
   * Closes the reader.
   *
   * @throws IOException if an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    this.reader.close();
  }
}
