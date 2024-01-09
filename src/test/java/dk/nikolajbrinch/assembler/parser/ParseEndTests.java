package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.operands.RegisterOperand;
import dk.nikolajbrinch.assembler.ast.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.scanner.Mnemonic;
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

    List<Statement> statements = new AssemblerParser().parse(tempFile.toFile());

    Assertions.assertEquals(1, statements.size());

    InstructionStatement instruction = (InstructionStatement) statements.get(0);
    Assertions.assertEquals(Mnemonic.EX, Mnemonic.find(instruction.mnemonic().text()));
    Assertions.assertEquals(Register.AF, ((RegisterOperand) instruction.operand1()).register());
    Assertions.assertEquals(
        Register.AF_QUOTE, ((RegisterOperand) instruction.operand2()).register());
  }
}
