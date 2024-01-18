package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.statements.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseMiscTests {

  @TempDir Path tempDir;

  //  @Disabled
  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
            org 0
            const equ 0x00
            var1:: set %00000001
            label1
            label::  ld a, $
            label    ld $$, d
            .align 256  ; this is a comment
            .local
            ; comments
            ld a, ($0034)
            .endlocal
            label:   ld d, e
            macro macro1 param1=0, param2=5
            label:   ld d, e
            endm
             macro macro2 param3, param4
            .endm
            rept $2 - $
            ld a, $34
            endr
            .phase $1000
            ; do this code in phase
            xor a, a ; zero a

            .dephase
            ; end

                        #end
            #if 1 == 1
            #endif
            ld a, 1 & 2
            ld b, 2 | 1
            ld c, 4 ^ 3
            ld a, 1 && '\\0'
            ld b, 2 || 1
            ld c, ~3
            ld c, '\\n'
            howdy: .byte 0x00, $12, %11111111
            """);

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile()).statements();

    for (Statement statement : statements) {
      System.out.println(new AssemblerAstPrinter().print(statement));
    }
  }
}
