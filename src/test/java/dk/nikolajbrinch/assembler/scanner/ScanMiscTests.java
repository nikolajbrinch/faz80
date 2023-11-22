package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScanMiscTests {

  @Test
  void testScan() throws IOException {
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
        ; end

                    #end

        .dephase
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
        """                .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }
}
