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

class ParseMacroCallTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
        macro macro1 a1, a2=0
        ld a, a1
        ld b, a2
        endm
        label: set 89
        macro1 1
        macro1 <ld a, b>, <2>
        macro1 (1, 2)
        macro1 (<1>, <"string">)
        macro1 label, <>
        ; comment
        """);

    List<Statement> statements = new MacroParser().parse(tempFile.toFile());

    String resolved =
        new MacroResolver().resolve(statements).stream().collect(Collectors.joining());
    Assertions.assertEquals(
        """

label: set 89
ld a,  1
ld b, 0

ld a, ld a, b
ld b, 2

ld a, 1
ld b,  2

ld a, 1
ld b, "string"

ld a, label
ld b,\s

; comment
""",
        resolved);
  }
}
