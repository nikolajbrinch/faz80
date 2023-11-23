package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.statements.InsertStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParseInsertTests {

  @Test
  void testParse() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
incbin "/Users/neko/somefile.z80"
""".getBytes(StandardCharsets.UTF_8));
        AssemblerScanner scanner = new AssemblerScanner(inputStream)) {

      List<Statement> statements = new AssemblerParser(scanner).parse();

      Assertions.assertEquals(1, statements.size());

      InsertStatement insert = (InsertStatement) statements.get(0);
      Assertions.assertEquals("\"/Users/neko/somefile.z80\"", insert.string().text());
    }
  }
}
