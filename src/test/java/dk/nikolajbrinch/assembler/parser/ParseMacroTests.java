package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.macro.parser.MacroParser;
import dk.nikolajbrinch.macro.preprocessor.MacroResolver;
import dk.nikolajbrinch.macro.scanner.MacroScanner;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseMacroTests {

  @TempDir Path tempDir;

  //  @Disabled
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

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile());
    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
