package dk.nikolajbrinch.faz80.parser.test;

import dk.nikolajbrinch.faz80.parser.AssemblerAstPrinter;
import dk.nikolajbrinch.faz80.parser.AssemblerParser;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParseFloatZ80Tests {

  @Test
  void testParse() throws IOException {
    List<Statement> statements =
        new AssemblerParser()
            .parse(new String(getClass().getResourceAsStream("/Float_z80.z80").readAllBytes()))
            .block()
            .statements();

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
