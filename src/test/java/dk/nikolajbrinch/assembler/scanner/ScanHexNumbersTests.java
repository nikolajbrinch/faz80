package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanHexNumbersTests {


  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
                var set 0xffff
                var set $ffff
                var set 0Xffff
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      Assertions.assertEquals(TokenType.HEX_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("ffff", scanner.peek(3).text());
      Assertions.assertEquals(TokenType.HEX_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("ffff", scanner.peek(7).text());
      Assertions.assertEquals(TokenType.HEX_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("ffff", scanner.peek(11).text());

      scanner.forEach(System.out::println);
    }
  }
}
