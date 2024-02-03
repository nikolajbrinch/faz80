package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.statements.AssignStatement;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
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

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile()).block().statements();

    Assertions.assertEquals(7, statements.size());
    Assertions.assertEquals("@id___:", ((AssignStatement) statements.get(0)).identifier().text());
    Assertions.assertEquals(".id", ((AssignStatement) statements.get(1)).identifier().text());
    Assertions.assertEquals("id:", ((AssignStatement) statements.get(2)).identifier().text());
    Assertions.assertEquals("id::", ((AssignStatement) statements.get(3)).identifier().text());
    Assertions.assertEquals("_id_1::", ((AssignStatement) statements.get(4)).identifier().text());
    Assertions.assertEquals("exx:", ((AssignStatement) statements.get(5)).identifier().text());
    Assertions.assertEquals("end:", ((AssignStatement) statements.get(6)).identifier().text());
  }
}
