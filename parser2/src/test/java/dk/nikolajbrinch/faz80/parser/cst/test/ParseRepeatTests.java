package dk.nikolajbrinch.faz80.parser.cst.test;

import dk.nikolajbrinch.faz80.parser.cst.NodePrinter;
import dk.nikolajbrinch.faz80.parser.cst.Parser;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParseRepeatTests {

  @TempDir Path tempDir;

  @Test
  void testParse() throws IOException {
    ProgramNode program =
        new Parser()
            .parse(
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
        """);

    System.out.println(new NodePrinter().print(program));

    Assertions.assertEquals(4, program.lines().lines().size());
  }
}
