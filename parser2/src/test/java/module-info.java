module dk.nikolajbrinch.faz80.parser2.test {
  requires dk.nikolajbrinch.faz80.scanner;
  requires dk.nikolajbrinch.faz80.parser.base;
  requires dk.nikolajbrinch.faz80.parser2;
  requires org.junit.jupiter.api;

  opens dk.nikolajbrinch.faz80.parser.cst.test to org.junit.platform.commons;
}
