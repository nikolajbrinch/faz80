package dk.nikolajbrinch.faz80.compiler;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompileTestProgram2Tests {

  @Test
  void testScan() throws IOException {
    Compiler compiler = new Compiler();
    compiler.compile(
        new String(getClass().getResource("/test-program-2.z80").openStream().readAllBytes()));

    Assertions.assertFalse(compiler.hasErrors());
  }
}
