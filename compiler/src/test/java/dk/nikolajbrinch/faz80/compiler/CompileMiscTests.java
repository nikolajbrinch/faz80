package dk.nikolajbrinch.faz80.compiler;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompileMiscTests {

  @Test
  void testScan() throws IOException {
    Compiler compiler = new Compiler();
    compiler.compile(new String(getClass().getResourceAsStream("/misc.z80").readAllBytes()));

    Assertions.assertFalse(compiler.hasErrors());
  }
}
