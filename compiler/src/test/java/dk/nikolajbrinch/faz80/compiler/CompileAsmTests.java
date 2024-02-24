package dk.nikolajbrinch.faz80.compiler;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class CompileAsmTests {

  @Test
  void testScan() throws IOException {
    Compiler compiler = new Compiler();
    compiler.compile(new String(getClass().getResourceAsStream("/ASM.z80").readAllBytes()));

    Assertions.assertFalse(compiler.hasErrors());
  }
}
