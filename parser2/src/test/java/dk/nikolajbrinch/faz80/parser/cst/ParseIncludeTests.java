package dk.nikolajbrinch.faz80.parser.cst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseIncludeTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path nextFile = Files.createFile(tempDir.resolve("next.z80"));
    Files.writeString(nextFile, """
    ld a, $10
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

    System.out.println(new NodePrinter().print(new Parser().parse(tempFile.toFile())));

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).lines().lines();

    Assertions.assertEquals(2, nodes.size());
  }

  @Test
  void testParseMacrosAndConstants() throws IOException {
    final Path nextFile = Files.createFile(tempDir.resolve("macros.z80"));
    Files.writeString(
        nextFile, """
    macro macro1 p1, p2
      ld a, p1
      ld b, p2
    endm
    """);

    final Path includeFile = Files.createFile(tempDir.resolve("constants.z80"));
    Files.writeString(includeFile, """
    constant1 equ 1
    constant2 equ 2
    """);

    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
    .include "macros.z80"
    .include "constants.z80"

    macro1 $10, 0b1010
    ld c, constant1
    ld d, constant2
    """);

    System.out.println(new NodePrinter().print(new Parser().parse(tempFile.toFile())));

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).lines().lines();

    Assertions.assertEquals(6, nodes.size());

    Assertions.assertEquals(
        7,
        new Parser(new ParserConfiguration(true, false))
            .parse(tempFile.toFile())
            .lines()
            .lines()
            .size());
  }
}
