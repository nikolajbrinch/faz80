package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParseMath48Tests {

  @Test
  void testParse() throws IOException {
    List<CstNode> nodes =
        new CstParser()
            .parse(new String(getClass().getResourceAsStream("/Math48.z80").readAllBytes()))
            .nodes()
            .nodes();

    for (CstNode node : nodes) {
      System.out.print(new CstPrinter().print(node));
    }
  }
}
