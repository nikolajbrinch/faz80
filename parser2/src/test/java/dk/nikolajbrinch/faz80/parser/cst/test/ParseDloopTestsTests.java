package dk.nikolajbrinch.faz80.parser.cst.test;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodePrinter;
import dk.nikolajbrinch.faz80.parser.cst.Parser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseDloopTestsTests {

  @TempDir
  File tempDir;

  @Test
  void testParse() throws IOException {
    File file =new File(tempDir, "dloop-tests.z80");
    Files.writeString(
        file.toPath(),
        new String(getClass().getResourceAsStream("/dloop-tests.z80").readAllBytes()));
    Files.writeString(
        new File(tempDir, "dloop-macros.z80").toPath(),
        new String(getClass().getResourceAsStream("/dloop-macros.z80").readAllBytes()));
    Files.writeString(
        new File(tempDir, "struct-macros.z80").toPath(),
        new String(getClass().getResourceAsStream("/struct-macros.z80").readAllBytes()));

    List<LineNode> nodes = new Parser().parse(file).lines().lines();

    for (Node node : nodes) {
      System.out.print(new NodePrinter().print(node));
    }
  }
}
