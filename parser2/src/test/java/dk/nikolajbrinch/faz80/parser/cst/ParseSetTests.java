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

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).node().lines();

    BasicLineNode lineNode = (BasicLineNode) nodes.get(0);
    Assertions.assertEquals("var1::", lineNode.label().label().text());
    Assertions.assertEquals(NodeType.VARIABLE, lineNode.instruction().type());

    lineNode = (BasicLineNode) nodes.get(1);
    Assertions.assertEquals("label1:", lineNode.label().label().text());
    Assertions.assertEquals(NodeType.OPCODE, lineNode.instruction().type());


    for (Node node : nodes) {
      System.out.print(new NodePrinter().print(node));
    }
  }
}
