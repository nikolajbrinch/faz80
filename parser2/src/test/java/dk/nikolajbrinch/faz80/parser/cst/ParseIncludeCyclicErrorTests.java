package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseIncludeCyclicErrorTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path nextFile = Files.createFile(tempDir.resolve("next.z80"));
    Files.writeString(nextFile, """
    ld a, $10
    include "include.z80"
    """);

    final Path includeFile = Files.createFile(tempDir.resolve("include.z80"));
    Files.writeString(includeFile, """
    ld a, b
    include "next.z80"
    """);

    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(tempFile, """
    .include "include.z80"
    """);

    Assertions.assertThrows(
        IllegalStateException.class, () -> new CstParser().parse(tempFile.toFile()));
  }
}
