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

class ParseMacroCallSpecialTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
        macro macro1 p1, p2, p3, p4
        ld a, p1
        ld b, p2
        ld c, p3
        ld d, p4
        endm
        label: set 89
        macro1 <label>, <>, <<>, <>>
        """);

    List<Statement> statements = new MacroParser().parse(tempFile.toFile());

    String resolved =
        new MacroResolver().resolve(statements).stream().collect(Collectors.joining());
    Assertions.assertEquals("""

label: set 89
ld a, label
ld b,\s
ld c, <
ld d, >

""", resolved);
  }
}
