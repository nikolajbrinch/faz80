package dk.nikolajbrinch.assembler.compiler;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class CompileRomTests {

  @Test
  void testScan() throws IOException {
    new Compiler().compile(new File(new File("."), "src/test/resources/rom.z80"));
  }
}
