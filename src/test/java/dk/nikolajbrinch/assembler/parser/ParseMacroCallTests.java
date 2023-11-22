package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.compiler.MacroResolver;
import dk.nikolajbrinch.assembler.scanner.Scanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ParseMacroCallTests {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        macro1 macro a1, a2=0
        endm
        label: set 89
        macro1 1
        macro1 <ld a, b>, <2>
        macro1 (1, 2)
        macro1 (<1>, <"string">)
        macro1 label, <>
        ; comment
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
