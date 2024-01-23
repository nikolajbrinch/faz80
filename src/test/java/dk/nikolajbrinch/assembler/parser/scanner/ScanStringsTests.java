package dk.nikolajbrinch.assembler.parser.scanner;

import dk.nikolajbrinch.parser.SourceInfo;
import dk.nikolajbrinch.parser.UrlSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanStringsTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new UrlSource(ScanDloopMacrosTests.class.getResource("/strings.z80")))) {

      Assertions.assertEquals(AssemblerTokenType.STRING, scanner.peek(2).type());
      Assertions.assertEquals("\"string\n\"\"", scanner.peek(2).text());
      Assertions.assertEquals(AssemblerTokenType.STRING, scanner.peek(5).type());
      Assertions.assertEquals("'string'\n'", scanner.peek(5).text());
      Assertions.assertEquals(AssemblerTokenType.CHAR, scanner.peek(8).type());
      Assertions.assertEquals("'a'", scanner.peek(8).text());
      Assertions.assertEquals(AssemblerTokenType.CHAR, scanner.peek(10).type());
      Assertions.assertEquals("'\n'", scanner.peek(10).text());
      Assertions.assertEquals(AssemblerTokenType.CHAR, scanner.peek(12).type());
      Assertions.assertEquals("'''", scanner.peek(12).text());

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(15, tokens.size());
    }
  }
}
