package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParseExpressionTests {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            0b10011001 + -8 * 0207 * (304 + 4) / 0x5a == 0o6 * {a + +b::} ^ [$4f << %1111111] >>> 3 & 7 | 9"""
                    .getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<Statement> statements = new AssemblerParser(scanner).parse();

      Assertions.assertEquals(
          "(expression (| (^ (== (+ 10011001 (/ (* (* (- 8) 207) (group (+ 304 4))) 5a)) (* 6 (group (+ (identifier: IDENTIFIER[@1:53-53(a)]) (+ (identifier: IDENTIFIER[@1:58-60(b::)])))))) (& (>>> (group (<< 4f 1111111)) 3) 7)) 9))",
          new AstPrinter().print(statements.get(0)));

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }
    }
  }
}
