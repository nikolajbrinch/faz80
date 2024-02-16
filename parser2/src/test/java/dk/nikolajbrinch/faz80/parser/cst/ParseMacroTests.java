package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseMacroTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
            mx = 0
            .macro macro1 param1=0, param2=4, param3='\\0'



            L:
            ld a, param1
            ld b, param2
            ld c, param3
            .endm
            mx = mx + 1

            macro1(1, 2, 4+3)
            """);

    List<CstNode> nodes = new CstParser().parse(tempFile.toFile()).nodes().nodes();

    for (CstNode node : nodes) {
      System.out.print(new CstPrinter().print(node));
    }
  }
}
