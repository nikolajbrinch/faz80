package dk.nikolajbrinch.faz80.parser.test;

import dk.nikolajbrinch.faz80.parser.AssemblerAstPrinter;
import dk.nikolajbrinch.faz80.parser.AssemblerParser;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
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

    List<Statement> statements = new AssemblerParser().parse(file).block().statements();

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
