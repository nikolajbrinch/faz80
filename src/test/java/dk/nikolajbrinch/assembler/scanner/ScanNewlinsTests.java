package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanNewlinsTests {

  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                "ld a, b\nld b, d\rld d, e\r\n".getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      Assertions.assertEquals(TokenType.NEWLINE, scanner.peek(5).type());
      Assertions.assertEquals(TokenType.NEWLINE, scanner.peek(10).type());
      Assertions.assertEquals(TokenType.NEWLINE, scanner.peek(15).type());

      scanner.forEach(System.out::println);
    }
  }

}
