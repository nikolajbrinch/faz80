package dk.nikolajbrinch.faz80.parser.cst.test;

import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.parser.cst.BasicLineNode;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.Parser;
import dk.nikolajbrinch.faz80.parser.cst.instructions.OpcodeNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.RegisterOperandNode;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseRegistersTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(tempFile, """
        ex af, af'
        """);

    List<LineNode> nodes = new Parser().parse(tempFile.toFile()).lines().lines();

    BasicLineNode line = (BasicLineNode) nodes.get(0);
    OpcodeNode instruction = (OpcodeNode) line.instruction();
    Assertions.assertEquals(Mnemonic.EX, Mnemonic.find(instruction.mnemonic().text()));
    Assertions.assertEquals(
        Register.AF,
        Register.find(
            ((RegisterOperandNode) instruction.operands().get(0)).register().text()));
    Assertions.assertEquals(
        Register.AF_QUOTE,
        Register.find(
            ((RegisterOperandNode) instruction.operands().get(1)).register().text()));
  }
}
