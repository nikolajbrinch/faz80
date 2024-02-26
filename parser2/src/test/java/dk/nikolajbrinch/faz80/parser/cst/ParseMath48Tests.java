package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParseMath48Tests {

  @Test
  void testParse() throws IOException {
    List<LineNode> nodes =
        new Parser()
            .parse(new String(getClass().getResourceAsStream("/Math48.z80").readAllBytes()))
            .lines()
            .lines();

    for (Node node : nodes) {
      System.out.print(new NodePrinter().print(node));
    }
  }
}
