package dk.nikolajbrinch.assembler.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanMath48Tests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream = ScanMath48Tests.class.getResourceAsStream("/Math48.z80");
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(6878, tokens.size());
    }
  }
}
