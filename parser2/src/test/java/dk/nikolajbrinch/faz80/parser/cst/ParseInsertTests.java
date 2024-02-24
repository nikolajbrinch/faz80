package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.expression.LiteralStringExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InsertNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseInsertTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(tempFile, """
incbin "/Users/neko/somefile.z80"
""");

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).node().lines();

    Assertions.assertEquals(1, nodes.size());

    BasicLineNode line = (BasicLineNode) nodes.get(0);

    InsertNode insert = (InsertNode) line.instruction();
    Assertions.assertEquals(
        "\"/Users/neko/somefile.z80\"",
        ((LiteralStringExpressionNode) insert.expression()).stringLiteral().text());
  }
}
