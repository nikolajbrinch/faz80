package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseIndexedTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(tempFile, """
        d set 5
        xor (ix+d)
        """);

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile());

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
