package dk.nikolajbrinch.assembler.scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ScanLabelsTests {

  @Test
  void testScan() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        0: ld a, 0x06
        _label: set 0b10101010
        global:: ld b, 0b
        label2 ld c, 9f
        9: org &1000
        ld c, 0f
        ld b, 99b
        .label3:
        0$: ld a, 9$"""
                    .getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      scanner.forEach(System.out::println);
    }
  }
}
