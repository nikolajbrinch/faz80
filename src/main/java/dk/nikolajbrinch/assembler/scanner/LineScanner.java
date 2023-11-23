package dk.nikolajbrinch.assembler.scanner;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LineScanner implements Iterable<Line>, AutoCloseable, Closeable {

  private final AssemblerScanner scanner;

  private boolean hasLines = true;

  private Line line = null;

  private final boolean filterComments = true;

  private final boolean filterNewlines = true;

  public LineScanner(AssemblerScanner scanner) {
    this.scanner = scanner;
  }

  public Line next() throws IOException {
    Line line = this.line;

    if (line == null) {
      line = read();
    }

    this.line = null;

    return line;
  }

  public boolean hasLines() throws IOException {
    Line line = this.line;

    if (line == null) {
      this.line = read();
    }

    return hasLines;
  }

  private Line read() throws IOException {
    Line line = null;

    while (line == null) {
      int lineNumber = 0;

      List<AssemblerToken> tokens = new ArrayList<>();

      do {
        AssemblerToken token = scanner.next();
        lineNumber = token.line();
        tokens.add(token);

        AssemblerTokenType type = token.type();

        if (type == AssemblerTokenType.NEWLINE || type == AssemblerTokenType.EOF) {
          if (type == AssemblerTokenType.EOF) {
            hasLines = false;
          }

          break;
        }
      } while (true);

      if (!hasLines) {
        break;
      }

      Stream<AssemblerToken> filterStream = tokens.stream();

      if (filterComments) {
        filterStream = filterStream.filter(token -> token.type() != AssemblerTokenType.COMMENT);
      }

      if (filterNewlines) {
        filterStream = filterStream.filter(token -> token.type() != AssemblerTokenType.NEWLINE);
      }

      List<AssemblerToken> filteredTokens = filterStream.toList();

      if (!filteredTokens.isEmpty()) {
        line = new Line(lineNumber, filteredTokens);
      }
    }

    return line;
  }

  @Override
  public Iterator<Line> iterator() {

    return new Iterator<Line>() {
      @Override
      public boolean hasNext() {
        try {
          return LineScanner.this.hasLines();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      @Override
      public Line next() {
        try {
          if (!LineScanner.this.hasLines()) {
            throw new NoSuchElementException("No more elements!");
          }

          return LineScanner.this.next();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    };
  }

  public Stream<Line> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  @Override
  public void close() throws IOException {
    scanner.close();
  }
}
