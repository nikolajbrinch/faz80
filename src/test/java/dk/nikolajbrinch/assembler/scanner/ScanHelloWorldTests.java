package dk.nikolajbrinch.assembler.scanner;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class ScanHelloWorldTests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream =
            ScanHelloWorldTests.class.getResourceAsStream("/hello-world.z80");
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }
}
