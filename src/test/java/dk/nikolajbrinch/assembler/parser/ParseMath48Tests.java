package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ParseMath48Tests {

  @Disabled
  @Test
  void testParse() throws IOException {
    try (InputStream inputStream = ParseMath48Tests.class.getResourceAsStream("/Math48.z80");
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<Statement> statements = new AssemblerParser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }
}
