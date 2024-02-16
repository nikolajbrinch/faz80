package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.Register;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RegisterTest {

  @Test
  void testFindRegisters() throws IOException {
    Assertions.assertEquals(Register.A, Register.find("a"));
    Assertions.assertEquals(Register.AF_QUOTE, Register.find("af'"));
  }
}
