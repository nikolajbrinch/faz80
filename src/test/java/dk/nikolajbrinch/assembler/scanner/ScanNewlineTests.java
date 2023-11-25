package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanNewlineTests {

  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                "ld a, b\nld b, d\rld d, e\r\n".getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      Assertions.assertEquals(AssemblerTokenType.NEWLINE, scanner.peek(5).type());
      Assertions.assertEquals(AssemblerTokenType.NEWLINE, scanner.peek(10).type());
      Assertions.assertEquals(AssemblerTokenType.NEWLINE, scanner.peek(15).type());

      scanner.forEach(System.out::println);
    }
  }
}
