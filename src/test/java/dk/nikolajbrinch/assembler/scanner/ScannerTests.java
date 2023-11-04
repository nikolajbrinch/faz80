package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScannerTests {

  @Test
  void testScanLabels() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        0: ld a, 0x06
        _label: set 0b10101010
        global:: ld b, 0b
        label2 ld c, 9f
        9: org &1000
        ld c, 0f
        ld b, 99b
        .label3:
        0$: ld a, 9$"""
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }

  @Test
  void testScanStrings() throws IOException {
    try (InputStream inputStream = ScannerTests.class.getResourceAsStream("/strings.z80");
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

  @Test
  void testScanOctalNumbers() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
                var set 0377
                var set 0o377
                var set 0O377
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      Assertions.assertEquals(TokenType.OCTAL_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("377", scanner.peek(3).text());
      Assertions.assertEquals(TokenType.OCTAL_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("377", scanner.peek(7).text());
      Assertions.assertEquals(TokenType.OCTAL_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("377", scanner.peek(11).text());

      scanner.forEach(System.out::println);
    }
  }

  @Test
  void testScanBinaryNumbers() throws IOException {
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

  @Test
  void testScanHexNumbers() throws IOException {
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

  @Test
  void testScanDecimalNumbers() throws IOException {
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

  @Test
  void testScanNewlines() throws IOException {
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

  @Test
  void testScanSimpleMonitor() throws IOException {
    try (InputStream inputStream = ScannerTests.class.getResourceAsStream("/simple-monitor.z80");
        Scanner scanner = new Scanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }

  @Test
  void testScanRom() throws IOException {
    try (InputStream inputStream = ScannerTests.class.getResourceAsStream("/rom.z80");
        Scanner scanner = new Scanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }

  @Test
  void testScanHelloWorld() throws IOException {
    try (InputStream inputStream = ScannerTests.class.getResourceAsStream("/hello-world.z80");
        Scanner scanner = new Scanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }

  @Test
  void testScanMath48() throws IOException {
    try (InputStream inputStream = ScannerTests.class.getResourceAsStream("/Math48.z80");
        Scanner scanner = new Scanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }
}
