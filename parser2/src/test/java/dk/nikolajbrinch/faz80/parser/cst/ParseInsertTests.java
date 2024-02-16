package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.expression.LiteralStringExpressionNode;
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

    List<CstNode> nodes = new CstParser().parse(tempFile.toFile()).nodes().nodes();

    Assertions.assertEquals(1, nodes.size());

    LineNode line = (LineNode) nodes.get(0);

    InsertNode insert = (InsertNode) line.command();
    Assertions.assertEquals(
        "\"/Users/neko/somefile.z80\"",
        ((LiteralStringExpressionNode) insert.expression()).stringLiteral().text());
  }
}
