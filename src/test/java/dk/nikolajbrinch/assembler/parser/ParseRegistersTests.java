package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.expressions.RegisterExpression;
import dk.nikolajbrinch.assembler.ast.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.compiler.MacroResolver;
import dk.nikolajbrinch.assembler.scanner.Mnemonic;
import dk.nikolajbrinch.assembler.scanner.Scanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ParseRegistersTests {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        ex af, af'
        """.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      InstructionStatement instruction = (InstructionStatement) statements.get(0);
      Assertions.assertEquals(Mnemonic.EX, Mnemonic.find(instruction.mnemonic().text()));
      Assertions.assertEquals(
          Register.AF, ((RegisterExpression) instruction.operand1()).register());
      Assertions.assertEquals(
          Register.AF_QUOTE, ((RegisterExpression) instruction.operand2()).register());
    }
  }
}
