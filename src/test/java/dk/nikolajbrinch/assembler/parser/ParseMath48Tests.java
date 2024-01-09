package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ParseMath48Tests {

  @Disabled
  @Test
  void testParse() throws IOException {
    File file = new File(ParseMath48Tests.class.getResource("/Math48.z80").toExternalForm());
    List<Statement> statements = new AssemblerParser().parse(file);

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
