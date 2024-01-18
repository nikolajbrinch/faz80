package dk.nikolajbrinch.assembler.parser.scanner;

import dk.nikolajbrinch.parser.SourceInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanOctalNumbersTests {

  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
                var set 0377
                var set 0o377
                var set 0O377
        """
                    .getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(new SourceInfo("name"), inputStream)) {

      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(3).type());
      Assertions.assertEquals("377", scanner.peek(3).text());
      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(7).type());
      Assertions.assertEquals("377", scanner.peek(7).text());
      Assertions.assertEquals(AssemblerTokenType.OCTAL_NUMBER, scanner.peek(11).type());
      Assertions.assertEquals("377", scanner.peek(11).text());

      List<AssemblerToken> tokens = new ArrayList<>();
      scanner.forEach(tokens::add);
      Assertions.assertEquals(13, tokens.size());
    }
  }
}
