package dk.nikolajbrinch.macro.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanMacroTests {

//  @Disabled
  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            mx = 0
            .macro macro1 param1=0, param2=4, param3='\\0'



            L:
            ld a, param1
            ld b, param2
            ld c, param3
            .endm
            mx = mx + 1

            macro1(1, 2, 4+3)
            """
                    .getBytes(StandardCharsets.UTF_8));
        MacroScanner scanner = new MacroScanner(inputStream)) {

      List<MacroToken> tokens = new ArrayList<>();
      scanner.forEach(
          token -> {
            System.out.print(token);
            tokens.add(token);
          });
      Assertions.assertEquals(69, tokens.size());
    }
  }
}
