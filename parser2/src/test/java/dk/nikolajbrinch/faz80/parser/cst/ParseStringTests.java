package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseStringTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(tempFile, """
            db "Hej" + 'ø' + "med dig"
            """);

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).node().lines();

    Assertions.assertEquals(
        "db \"Hej\" + 'ø' + \"med dig\"",
        new NodePrinter().print(((BasicLineNode) nodes.get(0)).instruction()));

    for (Node node : nodes) {
      System.out.print(new NodePrinter().print(node));
    }
  }
}
