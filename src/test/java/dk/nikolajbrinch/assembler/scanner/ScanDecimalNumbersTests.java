package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanDecimalNumbersTests {

  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
                var set 0
                var set 1234567890
                var set 91
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      Assertions.assertEquals(TokenType.DECIMAL_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("0", scanner.peek(3).text());
      Assertions.assertEquals(TokenType.DECIMAL_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("1234567890", scanner.peek(7).text());
      Assertions.assertEquals(TokenType.DECIMAL_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("91", scanner.peek(11).text());

      scanner.forEach(System.out::println);
    }
  }

}
