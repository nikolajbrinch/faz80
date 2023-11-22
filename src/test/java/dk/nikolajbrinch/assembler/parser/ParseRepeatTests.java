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

class ParseRepeatTests {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        count1 equ 4
        rept count1
        label: set 89
        ; comment
        endr
        count2 set 5
        rept count2
        label: set 89
        ; comment
        endr
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      List<Statement> resolved = new MacroResolver(new ExpressionEvaluator()).resolve(statements);
      for (Statement statement : resolved) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      new Compiler().compile(statements);
    }
  }

}
