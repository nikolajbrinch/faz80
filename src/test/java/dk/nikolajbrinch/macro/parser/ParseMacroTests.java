package dk.nikolajbrinch.macro.parser;

import dk.nikolajbrinch.macro.preprocessor.MacroResolver;
import dk.nikolajbrinch.macro.statements.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
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
            ; a comment


            L:
            ld a, param1
            ld b, param2
            param3
            .endm
            mx = mx + 1
            ; call the macro
            macro1(1, 2, <ld c, 4+3>)
            """);

    List<Statement> statements = new MacroParser().parse(tempFile.toFile());

    String resolved =
        new MacroResolver().resolve(statements).stream().collect(Collectors.joining());
    Assertions.assertEquals(
        """
mx = 0

mx = mx + 1
; call the macro
; a comment


L:
ld a, 1
ld b,  2
ld c, 4+3

""",
        resolved);
  }
}
