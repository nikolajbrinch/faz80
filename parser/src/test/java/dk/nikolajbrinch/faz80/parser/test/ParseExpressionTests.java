package dk.nikolajbrinch.faz80.parser.test;

import dk.nikolajbrinch.faz80.parser.AssemblerAstPrinter;
import dk.nikolajbrinch.faz80.parser.AssemblerParser;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseExpressionTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
            0b10011001 + -8 * 0207 * (304 + 4) / 0x5a == 0o6 * {a + +b::} ^ [$4f << %1111111] >>> 3 & 7 | 9""");

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile()).block().statements();

    Assertions.assertEquals(
        "(expression (| (^ (== (+ 0b10011001 (/ (* (* (- 8) 0207) (group (+ 304 4))) 0x5a)) (* 0o6 (group (+ identifier: a (+ identifier: b::))))) (& (>>> (group (<< $4f %1111111)) 3) 7)) 9))",
        new AssemblerAstPrinter().print(statements.get(0)));

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
