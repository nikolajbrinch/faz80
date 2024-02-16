package dk.nikolajbrinch.faz80.parser.cst;

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
        new CstParser()
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

    System.out.println(new CstPrinter().print(program));

    Assertions.assertEquals(4, program.nodes().nodes().size());
  }
}
