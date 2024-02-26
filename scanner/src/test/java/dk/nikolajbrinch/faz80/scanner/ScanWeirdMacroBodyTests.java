package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanWeirdMacroBodyTests {

  @Test
  void testScanChar() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new StringSource(
                """
             db "endm"
      label2 .endm
      """))) {

      scanner.setMode(Mode.MACRO_BODY);
      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(token -> {
        tokens.add(token);
      });

      Assertions.assertEquals(AssemblerTokenType.TEXT, tokens.get(0).type());
      Assertions.assertEquals(AssemblerTokenType.IDENTIFIER, tokens.get(1).type());
      Assertions.assertEquals(AssemblerTokenType.ENDMACRO, tokens.get(2).type());
      Assertions.assertEquals(AssemblerTokenType.NEWLINE, tokens.get(3).type());
      Assertions.assertEquals(AssemblerTokenType.EOF, tokens.get(4).type());
      Assertions.assertEquals("       db \"endm\"\n", tokens.get(0).text());
      Assertions.assertEquals("label2", tokens.get(1).text());
      Assertions.assertEquals(".endm", tokens.get(2).text());
      Assertions.assertEquals("\n", tokens.get(3).text());
      Assertions.assertEquals("", tokens.get(4).text());
    }
  }
}
