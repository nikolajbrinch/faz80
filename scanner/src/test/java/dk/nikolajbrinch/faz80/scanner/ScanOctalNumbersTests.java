package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanOctalNumbersTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new StringSource(
                """
                var set 0377
                var set 0o377
                var set 0O377
                var set 377O
                var set 377o
                var set 377q
                var set 377Q
        """))) {

      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("0377", scanner.peek(3).text());
      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("0o377", scanner.peek(7).text());
      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("0O377", scanner.peek(11).text());
      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(15).type());
      Assertions.assertEquals("377O", scanner.peek(15).text());
      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(19).type());
      Assertions.assertEquals("377o", scanner.peek(19).text());
      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(23).type());
      Assertions.assertEquals("377q", scanner.peek(23).text());
      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(27).type());
      Assertions.assertEquals("377Q", scanner.peek(27).text());

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      for (int i = 0; i < tokens.size(); i++) {
        System.out.println(
            String.format(
                "%d : \"%s\" (%s)", i + 1, tokens.get(i).text(), tokens.get(i).type().name()));
      }
      Assertions.assertEquals(29, tokens.size());
    }
  }
}
