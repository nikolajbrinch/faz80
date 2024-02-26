package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseExpressionTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
            ld hl, 0b10011001 + -8 * 0207 * (304 + 4) / 0x5a == 0o6 * {a + +b::} ^ [$4f << %1111111] >>> 3 & 7 | 9""");

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).lines().lines();

    Assertions.assertEquals(
        "ld hl, 0b10011001 + -8 * 0207 * (304 + 4) / 0x5a == 0o6 * {a + +b::} ^ [$4f << %1111111] >>> 3 & 7 | 9",
        new NodePrinter().print(((BasicLineNode) nodes.get(0)).instruction()));

    for (Node node : nodes) {
      System.out.print(new NodePrinter().print(node));
    }
  }
}
