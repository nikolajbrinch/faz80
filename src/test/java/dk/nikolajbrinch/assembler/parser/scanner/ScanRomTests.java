package dk.nikolajbrinch.assembler.parser.scanner;

import dk.nikolajbrinch.parser.UrlSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanRomTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(new UrlSource(ScanDloopMacrosTests.class.getResource("/rom.z80")))) {

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(282, tokens.size());
    }
  }
}
