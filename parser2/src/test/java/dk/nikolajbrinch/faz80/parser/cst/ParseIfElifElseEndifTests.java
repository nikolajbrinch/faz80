package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseIfElifElseEndifTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
            #if 1 == 1 ; 1 == 1, bogus test
            var1:: set 0x1000 ; We do have some comments
            #elif 2 == 2 ; try else if
            ld a, c
            #else ; else?
            ld a, d
            #endif ; we end it
            """);

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).lines().lines();

    for (Node node : nodes) {
      System.out.print(new NodePrinter().print(node));
    }
  }
}
