package dk.nikolajbrinch.assembler.scanner;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class LineScanHelloWorldTests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream =
            ScanHelloWorldTests.class.getResourceAsStream("/hello-world.z80");
        LineScanner scanner = new LineScanner(new AssemblerScanner(inputStream))) {

      for (Line line : scanner) {
        System.out.println(line);
      }
    }
  }
}
