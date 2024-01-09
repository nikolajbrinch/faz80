package dk.nikolajbrinch.assembler.compiler;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CompileMath48Tests {

  @Disabled
  @Test
  void testCompile() throws IOException {
    new Compiler().compile(new File(new File("."), "src/test/resources/Math48.z80"));
  }
}
