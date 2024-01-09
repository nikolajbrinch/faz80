package dk.nikolajbrinch.macro.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanMacroCallSpecialTests {

  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        macro1 macro p1, p2, p3, p4
        ld a, p1
        ld b, p2
        ld c, p3
        ld d, p4
        endm
        label: set 89
        macro1 <label>, <>, <<>, <>>
        """
                    .getBytes(StandardCharsets.UTF_8));
        MacroScanner scanner = new MacroScanner(inputStream)) {

      List<MacroToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(71, tokens.size());
    }
  }
}
