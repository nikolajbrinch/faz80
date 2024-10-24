package dk.nikolajbrinch.faz80.scanner.test;

import dk.nikolajbrinch.faz80.scanner.AssemblerScanner;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanLabelsTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new StringSource(
                """
          0: ld a, 0x06
          _label: set 0b10101010
          global:: ld b, 0b
          label2 ld c, 9f
          9: org &1000
          ld c, 0f
          ld b, 99b
          .label3:
          0$: ld a, 9$"""))) {

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(44, tokens.size());
    }
  }
}
