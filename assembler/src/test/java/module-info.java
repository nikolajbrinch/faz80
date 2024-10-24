module dk.nikolajbrinch.faz80.assembler.test {
  requires dk.nikolajbrinch.faz80.base;
  requires dk.nikolajbrinch.faz80.parser.base;
  requires dk.nikolajbrinch.faz80.parser;
  requires dk.nikolajbrinch.faz80.assembler;
  requires org.junit.jupiter.api;

  opens dk.nikolajbrinch.faz80.assembler.test.operands to
      org.junit.platform.commons;
}
