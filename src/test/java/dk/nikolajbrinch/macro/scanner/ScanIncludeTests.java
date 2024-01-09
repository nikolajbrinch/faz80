package dk.nikolajbrinch.macro.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanIncludeTests {

  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
    .include "/Users/neko/somefile.z80"
    """
                    .getBytes(StandardCharsets.UTF_8));
        MacroScanner scanner = new MacroScanner(inputStream)) {

      List<MacroToken> tokens = new ArrayList<>();
      scanner.forEach(
          token -> {
            System.out.print(token);
            tokens.add(token);
          });
      Assertions.assertEquals(5, tokens.size());
    }
  }
}
