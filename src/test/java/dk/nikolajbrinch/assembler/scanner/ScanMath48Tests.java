package dk.nikolajbrinch.assembler.scanner;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class ScanMath48Tests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream = ScanMath48Tests.class.getResourceAsStream("/Math48.z80");
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }
}
