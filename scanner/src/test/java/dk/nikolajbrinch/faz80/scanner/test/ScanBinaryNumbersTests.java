package dk.nikolajbrinch.faz80.scanner.test;

import dk.nikolajbrinch.faz80.scanner.AssemblerScanner;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanBinaryNumbersTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new StringSource(
                """
                var set 0b10000000
                var set %10000000
                var set 0B10000000
                var set 010000000B
                var set 010000000b
                var set 1b
        """))) {

//      Assertions.assertEquals(AssemblerTokenType.BINARY_NUMBER, scanner.peek(3).type());
//      Assertions.assertEquals("0b10000000", scanner.peek(3).text());
//      Assertions.assertEquals(AssemblerTokenType.BINARY_NUMBER, scanner.peek(7).type());
//      Assertions.assertEquals("%10000000", scanner.peek(7).text());
//      Assertions.assertEquals(AssemblerTokenType.BINARY_NUMBER, scanner.peek(11).type());
//      Assertions.assertEquals("0B10000000", scanner.peek(11).text());
//      Assertions.assertEquals(AssemblerTokenType.BINARY_NUMBER, scanner.peek(15).type());
//      Assertions.assertEquals("010000000B", scanner.peek(15).text());
//      Assertions.assertEquals(AssemblerTokenType.BINARY_NUMBER, scanner.peek(19).type());
//      Assertions.assertEquals("010000000b", scanner.peek(19).text());
//      Assertions.assertEquals(AssemblerTokenType.BINARY_NUMBER, scanner.peek(23).type());
//      Assertions.assertEquals("1b", scanner.peek(23).text());

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      for (int i = 0; i < tokens.size(); i++) {
        System.out.println(
            String.format(
                "%d : \"%s\" (%s)", i + 1, tokens.get(i).text(), tokens.get(i).type().name()));
      }
      Assertions.assertEquals(25, tokens.size());
    }
  }
}
