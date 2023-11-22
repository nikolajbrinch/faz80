package dk.nikolajbrinch.assembler.scanner;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class LineScanRomTests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream = ScanRomTests.class.getResourceAsStream("/rom.z80");
        LineScanner scanner = new LineScanner(new Scanner(inputStream))) {

      for (Line line : scanner) {
        System.out.println(line);
      }
    }
  }
}
