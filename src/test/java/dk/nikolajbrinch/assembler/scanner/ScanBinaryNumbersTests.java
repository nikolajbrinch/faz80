package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanBinaryNumbersTests {


  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
                var set 0b10000000
                var set %10000000
                var set 0B10000000
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      Assertions.assertEquals(TokenType.BINARY_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("10000000", scanner.peek(3).text());
      Assertions.assertEquals(TokenType.BINARY_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("10000000", scanner.peek(7).text());
      Assertions.assertEquals(TokenType.BINARY_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("10000000", scanner.peek(11).text());

      scanner.forEach(System.out::println);
    }
  }
}
