package dk.nikolajbrinch.parser.impl;

import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.CharReader;
import dk.nikolajbrinch.scanner.impl.BufferedCharReaderImpl;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BufferedCharReaderImplTests {

  @Test
  void testRead() throws Exception {
    String textToScan = """
  textToScan
  second line
  """;

    try (CharReader charReader =
        new BufferedCharReaderImpl(new ByteArrayInputStream(textToScan.getBytes(StandardCharsets.UTF_8)))) {
      Assertions.assertTrue(charReader.hasNext());

      Assertions.assertEquals('t', charReader.peek().character());
      Assertions.assertEquals(1, charReader.peek().line().number());
      Assertions.assertEquals(1, charReader.peek().column());

      Assertions.assertEquals('t', charReader.peek(4).character());
      Assertions.assertEquals(1, charReader.peek(4).line().number());
      Assertions.assertEquals(4, charReader.peek(4).column());

      Assertions.assertEquals('t', charReader.next().character());

      Assertions.assertEquals('t', charReader.peek(3).character());
      Assertions.assertEquals(1, charReader.peek(3).line().number());
      Assertions.assertEquals(4, charReader.peek(3).column());

      Char last = null;
      while (charReader.hasNext()) {
        last = charReader.next();
      }

      Assertions.assertEquals('\n', last.character());
      Assertions.assertEquals(2, last.line().number());
      Assertions.assertEquals(12, last.column());
      Assertions.assertNull(charReader.peek());
    }
  }

  @Test
  void testIterator() throws Exception {
    String textToScan = """
  textToScan
  second line
  """;

    try (CharReader charReader =
        new BufferedCharReaderImpl(new ByteArrayInputStream(textToScan.getBytes(StandardCharsets.UTF_8)))) {
      Iterator<Char> iterator = charReader.iterator();

      Char last = null;

      while (iterator.hasNext()) {
        last = iterator.next();
      }

      Assertions.assertEquals('\n', last.character());
      Assertions.assertEquals(2, last.line().number());
      Assertions.assertEquals(12, last.column());
      Assertions.assertThrows(NoSuchElementException.class, () -> iterator.next());
    }
  }

  @Test
  void testStream() throws Exception {
    String textToScan = """
  textToScan
  second line
  """;

    try (BufferedCharReaderImpl charReader =
        new BufferedCharReaderImpl(new ByteArrayInputStream(textToScan.getBytes(StandardCharsets.UTF_8)))) {
      StringBuilder builder = new StringBuilder();

      charReader.stream().map(read -> read.character()).forEach(read -> builder.append(read));

      Assertions.assertEquals(textToScan, builder.toString());
    }
  }
}
