package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ParseDloopMacrosTests {

  @Test
  void testParse() throws IOException {
    ProgramNode program =
        new CstParser()
            .parse(new String(getClass().getResourceAsStream("/dloop-macros.z80").readAllBytes()));

    System.out.println(new CstPrinter().print(program));
  }
}
