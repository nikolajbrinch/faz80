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

    List<CstNode> nodes = new CstParser().parse(tempFile.toFile()).nodes().nodes();

    Assertions.assertEquals(
        "db \"Hej\" + 'ø' + \"med dig\"",
        new CstPrinter().print(((LineNode) nodes.get(0)).command()));

    for (CstNode node : nodes) {
      System.out.print(new CstPrinter().print(node));
    }
  }
}
