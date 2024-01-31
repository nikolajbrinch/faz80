package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.statements.Statement;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParseMath48Tests {

  @Test
  void testParse() throws IOException {
    File file = new File(new File("."), "src/test/resources/Math48.z80");
    List<Statement> statements = new AssemblerParser().parse(file).block().statements();

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
