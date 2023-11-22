package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanStringsTests {

  @Test
  void testScan() throws IOException {
    try (InputStream inputStream = ScanStringsTests.class.getResourceAsStream("/strings.z80");
        Scanner scanner = new Scanner(inputStream)) {

      Assertions.assertEquals(TokenType.STRING, scanner.peek(2).type());
      Assertions.assertEquals("\"string\n\"\"", scanner.peek(2).text());
      Assertions.assertEquals(TokenType.STRING, scanner.peek(5).type());
      Assertions.assertEquals("'string'\n'", scanner.peek(5).text());
      Assertions.assertEquals(TokenType.CHAR, scanner.peek(8).type());
      Assertions.assertEquals("'a'", scanner.peek(8).text());
      Assertions.assertEquals(TokenType.CHAR, scanner.peek(10).type());
      Assertions.assertEquals("'\n'", scanner.peek(10).text());
      Assertions.assertEquals(TokenType.CHAR, scanner.peek(12).type());
      Assertions.assertEquals("'''", scanner.peek(12).text());

      scanner.forEach(System.out::println);
    }
  }
}
