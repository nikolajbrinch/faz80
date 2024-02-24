package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanUnterminatedStringsTests {

  @Test
  void testScanChar() throws IOException {
    try (AssemblerScanner scanner = new AssemblerScanner(new StringSource("ld a, '\\n\r\n"))) {

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(5, tokens.size());

      Assertions.assertEquals(AssemblerTokenType.NEWLINE, tokens.get(tokens.size() - 2).type());
      Assertions.assertEquals("\r\n", tokens.get(tokens.size() - 2).text());
      Assertions.assertEquals(AssemblerTokenType.EOF, tokens.get(tokens.size() - 1).type());

      Assertions.assertEquals(1, scanner.getErrors().size());
      Assertions.assertEquals("'\n", scanner.getErrors().get(0).errorToken().text());
      Assertions.assertEquals(7, scanner.getErrors().get(0).errorToken().start());
      Assertions.assertEquals(8, scanner.getErrors().get(0).errorToken().end());
      Assertions.assertEquals(1, scanner.getErrors().get(0).errorToken().line().number());
    }
  }

  @Test
  void testScanData() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new StringSource(
                "label1: db \"hej med dig her er en streng der ikke er termineret"))) {

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(3, tokens.size());

      Assertions.assertEquals(AssemblerTokenType.EOF, tokens.get(tokens.size() - 1).type());

      Assertions.assertEquals(1, scanner.getErrors().size());
      Assertions.assertEquals(
          "\"hej med dig her er en streng der ikke er termineret",
          scanner.getErrors().get(0).errorToken().text());
      Assertions.assertEquals(12, scanner.getErrors().get(0).errorToken().start());
      Assertions.assertEquals(63, scanner.getErrors().get(0).errorToken().end());
      Assertions.assertEquals(1, scanner.getErrors().get(0).errorToken().line().number());
    }
  }
}
