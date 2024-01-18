package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.statements.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseMacroCallSpecialTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
        macro macro1 p1, p2, p3, p4
        ld a, p1
        ld b, p2
        ld c, p3
        ld d, p4
        endm
        label: set 89
        macro1 <label>, <>, <<>, <>>
        """);

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile()).statements();

    System.out.println("-----");

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }

    System.out.println("-----");
  }
}
