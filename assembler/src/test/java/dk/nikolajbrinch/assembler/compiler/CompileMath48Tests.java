package dk.nikolajbrinch.assembler.compiler;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompileMath48Tests {

  @Test
  void testCompile() throws IOException {
    Compiler compiler = new Compiler();
    compiler.compile(new File(new File("."), "src/test/resources/Math48.z80"));

    Assertions.assertFalse(compiler.hasErrors());
  }
}
