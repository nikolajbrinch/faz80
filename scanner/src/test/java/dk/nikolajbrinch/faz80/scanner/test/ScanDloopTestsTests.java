package dk.nikolajbrinch.faz80.scanner.test;

import dk.nikolajbrinch.faz80.scanner.AssemblerScanner;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.impl.UrlSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanDloopTestsTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new UrlSource(ScanDloopTestsTests.class.getResource("/dloop-tests.z80")))) {

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(148, tokens.size());
    }
  }
}
