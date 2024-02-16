package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.Register;
import dk.nikolajbrinch.faz80.parser.cst.operands.RegisterOperandNode;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseEndTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    final Path tempFile = Files.createFile(tempDir.resolve("code.z80"));
    Files.writeString(
        tempFile,
        """
        ex af, af'
        ; comment
        #end
        ; another comment
        ld a, b
        """);

    List<CstNode> nodes = new CstParser().parse(tempFile.toFile()).nodes().nodes();

    Assertions.assertEquals(5, nodes.size());

    LineNode line = (LineNode) nodes.get(0);
    InstructionNode instruction = (InstructionNode) line.command();
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
