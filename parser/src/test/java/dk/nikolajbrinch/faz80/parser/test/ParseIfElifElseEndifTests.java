package dk.nikolajbrinch.faz80.parser.test;

import dk.nikolajbrinch.faz80.parser.AssemblerAstPrinter;
import dk.nikolajbrinch.faz80.parser.AssemblerParser;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
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
            #if 1 == 1
            var1:: set 0x1000
            #elif 2 == 2
            ld a, c
            #else
            ld a, d
            #endif
            """);

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile()).block().statements();

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
