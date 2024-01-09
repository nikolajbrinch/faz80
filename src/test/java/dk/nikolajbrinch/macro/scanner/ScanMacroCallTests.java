package dk.nikolajbrinch.macro.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanMacroCallTests {

  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        macro1 macro a1, a2=0
        endm
        label: set 89
        macro1 1
        macro1 <ld a, b>, <2>
        macro1 (1, 2)
        macro1 (<1>, <"string">)
        macro1 label, <>
        ; comment
        """
                    .getBytes(StandardCharsets.UTF_8));
        MacroScanner scanner = new MacroScanner(inputStream)) {

      List<MacroToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(69, tokens.size());
    }
  }
}
