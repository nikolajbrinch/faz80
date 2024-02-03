package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanHexNumbersTests {

  @Test
  void testScan() throws IOException {
    try (AssemblerScanner scanner =
        new AssemblerScanner(
            new StringSource(
                """
                  var set 0xffff
                  var set $ffff
                  var set 0Xffff
                  var set 0ffffH
                  var set 80h
                  var set 0FFh
          """))) {

      Assertions.assertEquals(AssemblerTokenType.HEX_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("0xffff", scanner.peek(3).text());
      Assertions.assertEquals(AssemblerTokenType.HEX_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("$ffff", scanner.peek(7).text());
      Assertions.assertEquals(AssemblerTokenType.HEX_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("0Xffff", scanner.peek(11).text());
      Assertions.assertEquals(AssemblerTokenType.HEX_NUMBER, scanner.peek(15).type());
      Assertions.assertEquals("0ffffH", scanner.peek(15).text());
      Assertions.assertEquals(AssemblerTokenType.HEX_NUMBER, scanner.peek(19).type());
      Assertions.assertEquals("80h", scanner.peek(19).text());
      Assertions.assertEquals(AssemblerTokenType.HEX_NUMBER, scanner.peek(23).type());
      Assertions.assertEquals("0FFh", scanner.peek(23).text());

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      for (int i = 0; i < tokens.size(); i++) {
        System.out.println(
            String.format(
                "%d : \"%s\" (%s)", i + 1, tokens.get(i).text(), tokens.get(i).type().name()));
      }
      Assertions.assertEquals(25, tokens.size());
    }
  }
}
