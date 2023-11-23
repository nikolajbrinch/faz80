package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.LabelStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParserIdentifiersTest {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
          @id___:
          .id
          id:
          id::
          _id_1::
          exx:
          end:
          """
                    .getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<Statement> statements = new AssemblerParser(scanner).parse();

      Assertions.assertEquals(7, statements.size());
      Assertions.assertEquals("@id___:", ((LabelStatement) statements.get(0)).identifier().text());
      Assertions.assertEquals(".id", ((LabelStatement) statements.get(1)).identifier().text());
      Assertions.assertEquals("id:", ((LabelStatement) statements.get(2)).identifier().text());
      Assertions.assertEquals("id::", ((LabelStatement) statements.get(3)).identifier().text());
      Assertions.assertEquals("_id_1::", ((LabelStatement) statements.get(4)).identifier().text());
      Assertions.assertEquals("exx:", ((LabelStatement) statements.get(5)).identifier().text());
      Assertions.assertEquals("end:", ((LabelStatement) statements.get(6)).identifier().text());
    }
  }
}
