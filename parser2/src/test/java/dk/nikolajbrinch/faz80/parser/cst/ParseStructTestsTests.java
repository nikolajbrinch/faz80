package dk.nikolajbrinch.faz80.parser.cst;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseStructTestsTests {

  @TempDir File tempDir;

  @Test
  void testParse() throws IOException {
    File file = new File(tempDir, "struct-tests.z80");
    Files.writeString(
        file.toPath(),
        new String(getClass().getResourceAsStream("/struct-tests.z80").readAllBytes()));
    Files.writeString(
        new File(tempDir, "struct-macros.z80").toPath(),
        new String(getClass().getResourceAsStream("/struct-macros.z80").readAllBytes()));

    List<CstNode> nodes = new CstParser().parse(file).nodes().nodes();

    for (CstNode node : nodes) {
      System.out.print(new CstPrinter().print(node));
    }
  }
}
