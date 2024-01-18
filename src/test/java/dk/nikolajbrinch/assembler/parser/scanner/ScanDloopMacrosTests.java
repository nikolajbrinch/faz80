package dk.nikolajbrinch.assembler.parser.scanner;

import dk.nikolajbrinch.parser.SourceInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanDloopMacrosTests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream =
            ScanDloopMacrosTests.class.getResourceAsStream("/dloop-macros.z80");
        AssemblerScanner scanner = new AssemblerScanner(new SourceInfo("name"), inputStream)) {

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);

      Assertions.assertEquals(344, tokens.size());
    }
  }
}
