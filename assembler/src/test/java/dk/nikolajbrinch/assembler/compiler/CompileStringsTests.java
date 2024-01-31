package dk.nikolajbrinch.assembler.compiler;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompileStringsTests {

  @Test
  void testScan() throws IOException {
    Compiler compiler = new Compiler();
    compiler.compile(new File(new File("."), "src/test/resources/strings.z80"));

    Assertions.assertFalse(compiler.hasErrors());
  }
}
