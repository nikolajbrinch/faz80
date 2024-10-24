package dk.nikolajbrinch.faz80.compiler.test;

import dk.nikolajbrinch.faz80.compiler.Compiler;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompileStringsTests {

  @Test
  void testScan() throws IOException {
    Compiler compiler = new Compiler();
    compiler.compile(new String(getClass().getResourceAsStream("/strings.z80").readAllBytes()));

    Assertions.assertFalse(compiler.hasErrors());
  }
}
