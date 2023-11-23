package dk.nikolajbrinch.assembler.scanner;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class ScanRomTests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream = ScanRomTests.class.getResourceAsStream("/rom.z80");
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }
}
