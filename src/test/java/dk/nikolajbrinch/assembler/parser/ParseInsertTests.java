package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.InsertStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseInsertTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(tempFile, """
incbin "/Users/neko/somefile.z80"
""");

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile());

    Assertions.assertEquals(1, statements.size());

    InsertStatement insert = (InsertStatement) statements.get(0);
    Assertions.assertEquals("\"/Users/neko/somefile.z80\"", insert.string().text());
  }
}
