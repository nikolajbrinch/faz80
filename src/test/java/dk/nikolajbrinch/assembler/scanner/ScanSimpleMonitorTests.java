package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanSimpleMonitorTests {


  @Test
  void testScan() throws IOException {
    try (InputStream inputStream = ScanSimpleMonitorTests.class.getResourceAsStream("/simple-monitor.z80");
        Scanner scanner = new Scanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }

}
