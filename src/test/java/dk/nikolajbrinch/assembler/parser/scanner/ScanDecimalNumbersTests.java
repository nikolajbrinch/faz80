package dk.nikolajbrinch.assembler.parser.scanner;

import dk.nikolajbrinch.parser.impl.StringSource;
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
        """))) {

      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("0", scanner.peek(3).text());
      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("1234567890", scanner.peek(7).text());
      Assertions.assertEquals(AssemblerTokenType.DECIMAL_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("91", scanner.peek(11).text());

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(13, tokens.size());
    }
  }
}
