package dk.nikolajbrinch.faz80.parser.cst.test;

import dk.nikolajbrinch.faz80.parser.cst.BasicLineNode;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.Parser;
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

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).lines().lines();

    Assertions.assertEquals(7, nodes.size());
    Assertions.assertEquals("@id___:", ((BasicLineNode) nodes.get(0)).label().label().text());
    Assertions.assertEquals(".id", ((BasicLineNode) nodes.get(1)).label().label().text());
    Assertions.assertEquals("id:", ((BasicLineNode) nodes.get(2)).label().label().text());
    Assertions.assertEquals("id::", ((BasicLineNode) nodes.get(3)).label().label().text());
    Assertions.assertEquals("_id_1::", ((BasicLineNode) nodes.get(4)).label().label().text());
    Assertions.assertEquals("exx:", ((BasicLineNode) nodes.get(5)).label().label().text());
    Assertions.assertEquals("end:", ((BasicLineNode) nodes.get(6)).label().label().text());
  }
}
