package dk.nikolajbrinch.faz80.parser.cst.test;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodePrinter;
import dk.nikolajbrinch.faz80.parser.cst.Parser;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParseStructMacrosTests {

  @Test
  void testParse() throws IOException {
    List<LineNode> nodes =
        new Parser()
            .parse(new String(getClass().getResourceAsStream("/struct-macros.z80").readAllBytes()))
            .lines()
            .lines();

    for (Node node : nodes) {
      System.out.print(new NodePrinter().print(node));
    }
  }
}
