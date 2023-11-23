package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.expressions.RegisterExpression;
import dk.nikolajbrinch.assembler.ast.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.scanner.Mnemonic;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParseEndTests {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        ex af, af'
        ; comment
        #end
        ; another comment
        ld a, b
        """
                    .getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<Statement> statements = new AssemblerParser(scanner).parse();

      Assertions.assertEquals(1, statements.size());

      InstructionStatement instruction = (InstructionStatement) statements.get(0);
      Assertions.assertEquals(Mnemonic.EX, Mnemonic.find(instruction.mnemonic().text()));
      Assertions.assertEquals(
          Register.AF, ((RegisterExpression) instruction.operand1()).register());
      Assertions.assertEquals(
          Register.AF_QUOTE, ((RegisterExpression) instruction.operand2()).register());
    }
  }
}
