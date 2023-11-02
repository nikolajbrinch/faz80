package dk.nikolajbrinch.assembler.reader;

import dk.nikolajbrinch.assembler.reader.CharReader.Char;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CharReaderTests {

  @Test
  void testScanner() throws Exception {
    String textToScan = """
  textToScan
  second line
  """;

    try (CharReader charReader = new CharReader(new ByteArrayInputStream(textToScan.getBytes(StandardCharsets.UTF_8)))) {
      Assertions.assertTrue(charReader.hasNext());

      Assertions.assertEquals('t', charReader.peek().character());
      Assertions.assertEquals(1, charReader.peek().line());
      Assertions.assertEquals(1, charReader.peek().position());

      Assertions.assertEquals('t', charReader.peek(4).character());
      Assertions.assertEquals(1, charReader.peek(4).line());
      Assertions.assertEquals(4, charReader.peek(4).position());

      Assertions.assertEquals('t', charReader.next().character());

      Assertions.assertEquals('t', charReader.peek(3).character());
      Assertions.assertEquals(1, charReader.peek(3).line());
      Assertions.assertEquals(4, charReader.peek(3).position());

      Char last = null;
      while (charReader.hasNext()) {
        last = charReader.next();
      }

      Assertions.assertEquals('\n', last.character());
      Assertions.assertEquals(2, last.line());
      Assertions.assertEquals(12, last.position());
      Assertions.assertNull(charReader.peek());
    }
  }

  @Test
  void testScannerIterator() throws Exception {
    String textToScan = """
  textToScan
  second line
  """;

    try (CharReader charReader = new CharReader(new ByteArrayInputStream(textToScan.getBytes(StandardCharsets.UTF_8)))) {
      Iterator<Char> iterator = charReader.iterator();

      Char last = null;

      while (iterator.hasNext()) {
        last = iterator.next();
      }

      Assertions.assertEquals('\n', last.character());
      Assertions.assertEquals(2, last.line());
      Assertions.assertEquals(12, last.position());
      Assertions.assertThrows(NoSuchElementException.class, () -> iterator.next());
    }
  }

  @Test
  void testScannerStream() throws Exception {
    String textToScan = """
  textToScan
  second line
  """;

    try (CharReader charReader = new CharReader(new ByteArrayInputStream(textToScan.getBytes(StandardCharsets.UTF_8)))) {
      StringBuilder builder = new StringBuilder();

      charReader.stream().map(read -> read.character()).forEach(read -> builder.append(read));

      Assertions.assertEquals(textToScan, builder.toString());
    }
  }
}
