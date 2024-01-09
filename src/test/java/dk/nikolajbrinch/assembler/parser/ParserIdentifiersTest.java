package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.LabelStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParserIdentifiersTest {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
          @id___:
          .id
          id:
          id::
          _id_1::
          exx:
          end:
          """);

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile());

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
