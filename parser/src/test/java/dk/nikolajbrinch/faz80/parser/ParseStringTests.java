package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.statements.Statement;
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
            "Hej" + 'ø' + "med dig"
            """);

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile()).block().statements();

    Assertions.assertEquals(
        "(expression (+ (+ \"Hej\" 'ø') \"med dig\"))",
        new AssemblerAstPrinter().print(statements.get(0)));

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
