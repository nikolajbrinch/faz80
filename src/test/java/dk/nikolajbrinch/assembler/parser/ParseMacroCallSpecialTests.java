package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.compiler.MacroResolver;
import dk.nikolajbrinch.assembler.scanner.Scanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ParseMacroCallSpecialTests {

  @Disabled
  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        macro1 macro p1, p2, p3, p4
        ld a, p1
        ld b, p2
        ld c, p3
        ld d, p4
        endm
        label: set 89
        macro1 <label>, <>, <<>, <>>
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
