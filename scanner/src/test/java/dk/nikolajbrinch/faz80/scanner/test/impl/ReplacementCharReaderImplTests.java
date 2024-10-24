package dk.nikolajbrinch.faz80.scanner.test.impl;

import dk.nikolajbrinch.scanner.Char;
import dk.nikolajbrinch.scanner.CharReader;
import dk.nikolajbrinch.scanner.impl.Marker;
import dk.nikolajbrinch.scanner.impl.ReplacementCharReaderImpl;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ReplacementCharReaderImplTests {

  @Test
  void testReplace() throws IOException {
    Map<String, Object> symbols = Map.of("p1", "FOO", "p2", "BAR");

    try (CharReader charReader =
        new ReplacementCharReaderImpl(
            """
          macro name p1=4, p2=5
lab&p1f:  db "${p2}"
          endm
    """,
            Set.of(new Marker("${", "}"), new Marker("&")),
            symbols)) {

      for (Char ch : charReader) {
        System.out.print(ch.character());
      }
    }
  }
}
