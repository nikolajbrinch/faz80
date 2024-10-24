package dk.nikolajbrinch.faz80.scanner.test;

import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;
import dk.nikolajbrinch.faz80.scanner.Directive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AssemblerTokenTypeTests {

  @Test
  void verifyTokenTypes() {
    Directive[] directives = Directive.values();

    for (Directive directive : directives) {
      Assertions.assertDoesNotThrow(() -> AssemblerTokenType.valueOf(directive.name()));
    }
  }
}
