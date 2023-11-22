package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanHelloWorldTests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream = ScanHelloWorldTests.class.getResourceAsStream("/hello-world.z80");
        Scanner scanner = new Scanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }

}
