package dk.nikolajbrinch.faz80.parser.base.test;

import dk.nikolajbrinch.faz80.parser.base.IdentifierNormalizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdentifierNormalizerTests {

  @Test
  void testNormalize() {
    Assertions.assertEquals(IdentifierNormalizer.normalize("foo"), "foo");
    Assertions.assertEquals(IdentifierNormalizer.normalize(".foo"), "foo");
    Assertions.assertEquals(IdentifierNormalizer.normalize("..foo"), "foo");
    Assertions.assertEquals(IdentifierNormalizer.normalize("foo:"), "foo");
    Assertions.assertEquals(IdentifierNormalizer.normalize("foo::"), "foo");
    Assertions.assertEquals(IdentifierNormalizer.normalize(".foo:"), "foo");
    Assertions.assertEquals(IdentifierNormalizer.normalize("..foo:"), "foo");
    Assertions.assertEquals(IdentifierNormalizer.normalize(".foo::"), "foo");
  }
}
