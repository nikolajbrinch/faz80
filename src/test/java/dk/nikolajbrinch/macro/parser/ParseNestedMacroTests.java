package dk.nikolajbrinch.macro.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.compiler.Assembler;
import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseNestedMacroTests {

  @TempDir Path tempDir;

  //  @Disabled
  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
            .macro macro1 param1=0, param2=4
            ld a, param1
            ld b, param2
            .endm
            macro macro2 param1
            ld c, param1
            macro1(1, 2)
            endm
            macro2 4+3
            """);

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile());

    new Assembler(new ExpressionEvaluator()).assemble(statements);

//    Assertions.assertEquals("""
//ld c, 4+3
//ld a, 1
//ld b,  2
//""", resolved);
  }
}
