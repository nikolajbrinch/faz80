package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ParseMiscTests {

//  @Disabled
  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
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
            macro2 macro param3, param4
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
            """
                    .getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<Statement> statements = new AssemblerParser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      Compiler compiler = new Compiler();
      compiler.compile(statements);

      Assertions.assertTrue(compiler.hasErrors());
    }
  }
}
