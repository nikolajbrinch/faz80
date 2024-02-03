package dk.nikolajbrinch.faz80.compiler;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompileHelloWorldTests {

  @Test
  void testCompile() throws IOException {
    Compiler compiler = new Compiler();
    compiler.compile(new String(getClass().getResourceAsStream("/hello-world.z80").readAllBytes()));

    Assertions.assertFalse(compiler.hasErrors());
  }
}
