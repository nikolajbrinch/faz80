package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.impl.UrlSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanDloopMacrosTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new UrlSource(ScanDloopMacrosTests.class.getResource("/dloop-macros.z80")))) {
      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);

      Assertions.assertEquals(328, tokens.size());
    }
  }
}
