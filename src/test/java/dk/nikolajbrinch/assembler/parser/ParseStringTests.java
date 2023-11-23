package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParseStringTests {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            "Hej" + 'ø' + "med dig"
            """
                    .getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<Statement> statements = new AssemblerParser(scanner).parse();

      Assertions.assertEquals(
          "(expression (+ (+ \"Hej\" 'ø') \"med dig\"))",
          new AstPrinter().print(statements.get(0)));

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }
    }
  }
}
