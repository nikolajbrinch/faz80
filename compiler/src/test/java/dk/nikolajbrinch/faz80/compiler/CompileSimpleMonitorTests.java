package dk.nikolajbrinch.faz80.compiler;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompileSimpleMonitorTests {

  @Test
  void testScan() throws IOException {
    Compiler compiler = new Compiler();
    compiler.compile(
        new String(getClass().getResourceAsStream("/simple-monitor.z80").readAllBytes()));

    Assertions.assertFalse(compiler.hasErrors());
  }
}
