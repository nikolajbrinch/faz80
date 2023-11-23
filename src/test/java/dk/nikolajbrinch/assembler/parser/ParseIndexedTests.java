package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParseIndexedTests {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        d set 5
        xor (ix+d)
        """
                    .getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<Statement> statements = new AssemblerParser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }
}
