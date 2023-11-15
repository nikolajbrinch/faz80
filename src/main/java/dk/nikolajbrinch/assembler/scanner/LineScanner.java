package dk.nikolajbrinch.assembler.scanner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LineScanner implements Iterable<Line> {

  private final Scanner scanner;

  private boolean hasLines = true;

  private Line line = null;

  private boolean filterComments = true;

  private boolean filterNewlines = true;

  public LineScanner(Scanner scanner) {
    this.scanner = scanner;
  }

  public Line nextLine() throws IOException {
    Line line = this.line;

    if (line == null) {
      line = doRead();
    }

    this.line = null;

    return line;
  }

  public boolean hasLines() throws IOException {
    Line line = this.line;

    if (line == null) {
      this.line = doRead();
    }

    return hasLines;
  }

  private Line doRead() throws IOException {
    Line line = null;

    while (line == null) {
      int lineNumber = 0;

      List<Token> tokens = new ArrayList<>();

      do {
        Token token = scanner.next();
        lineNumber = token.line();
        tokens.add(token);

        TokenType type = token.type();

        if (type == TokenType.NEWLINE || type == TokenType.EOF) {
          if (type == TokenType.EOF) {
            hasLines = false;
          }

          break;
        }
      } while (true);

      if (!hasLines) {
        break;
      }

      Stream<Token> filterStream = tokens.stream();

      if (filterComments) {
        filterStream = filterStream.filter(token -> token.type() != TokenType.COMMENT);
      }

      if (filterNewlines) {
        filterStream = filterStream.filter(token -> token.type() != TokenType.NEWLINE);
      }

      List<Token> filteredTokens = filterStream.toList();

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

          return LineScanner.this.nextLine();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    };
  }

  public Stream<Line> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }
}
