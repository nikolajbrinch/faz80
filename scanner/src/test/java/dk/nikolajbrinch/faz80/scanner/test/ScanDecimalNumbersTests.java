package dk.nikolajbrinch.faz80.scanner.test;

import dk.nikolajbrinch.faz80.scanner.AssemblerScanner;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;
import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanDecimalNumbersTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new StringSource(
                """
                var set 0
                var set 1234567890
                var set 91
                var set 91D
                var set 91d
                var set 091D
                var set 091d
        """))) {

      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("0", scanner.peek(3).text());
      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("1234567890", scanner.peek(7).text());
      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("91", scanner.peek(11).text());
      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(15).type());
      Assertions.assertEquals("91D", scanner.peek(15).text());
      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(19).type());
      Assertions.assertEquals("91d", scanner.peek(19).text());
      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(23).type());
      Assertions.assertEquals("091D", scanner.peek(23).text());
      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(27).type());
      Assertions.assertEquals("091d", scanner.peek(27).text());

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      for (int i = 0; i < tokens.size(); i++) {
        System.out.println(
            String.format(
                "%d : \"%s\" (%s)", i + 1, tokens.get(i).text(), tokens.get(i).type().name()));
      }
      Assertions.assertEquals(29, tokens.size());
    }
  }
}
