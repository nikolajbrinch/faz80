package dk.nikolajbrinch.macro.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanNestedMacro {

//  @Disabled
  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            .macro macro1 param1=0, param2=4
            ld a, param1
            ld b, param2
            .endm
            macro macro2 param1
            ld c, param1
            macro1(1, 2)
            endm
            macro2 4+3
            """
                    .getBytes(StandardCharsets.UTF_8));
        MacroScanner scanner = new MacroScanner(inputStream)) {

      List<MacroToken> tokens = new ArrayList<>();
      scanner.forEach(
          token -> {
            System.out.println(token);
            tokens.add(token);
          });
      Assertions.assertEquals(54, tokens.size());
    }
  }
}
