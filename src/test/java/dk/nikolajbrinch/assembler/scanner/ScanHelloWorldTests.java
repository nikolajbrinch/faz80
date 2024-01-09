package dk.nikolajbrinch.assembler.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanHelloWorldTests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream =
            ScanHelloWorldTests.class.getResourceAsStream("/hello-world.z80");
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(56, tokens.size());
    }
  }
}
