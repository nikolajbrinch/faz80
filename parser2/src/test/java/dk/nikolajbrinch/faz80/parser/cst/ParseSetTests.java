package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseSetTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
            var1:: set %00000001
            label1: set 4, a
            """);

    List<CstNode> nodes = new CstParser().parse(tempFile.toFile()).nodes().nodes();

    LineNode lineNode = (LineNode) nodes.get(0);
    Assertions.assertEquals("var1::", lineNode.label().label().text());
    Assertions.assertEquals(NodeType.VARIABLE, lineNode.command().type());

    lineNode = (LineNode) nodes.get(1);
    Assertions.assertEquals("label1:", lineNode.label().label().text());
    Assertions.assertEquals(NodeType.INSTRUCTION, lineNode.command().type());


    for (CstNode node : nodes) {
      System.out.print(new CstPrinter().print(node));
    }
  }
}
