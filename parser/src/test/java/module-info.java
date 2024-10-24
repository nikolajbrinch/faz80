module dk.nikolajbrinch.faz80.parser.test {
  requires dk.nikolajbrinch.faz80.scanner;
  requires dk.nikolajbrinch.faz80.parser.base;
  requires dk.nikolajbrinch.faz80.parser;
  requires org.junit.jupiter.api;

  opens dk.nikolajbrinch.faz80.parser.test to org.junit.platform.commons;
  opens dk.nikolajbrinch.faz80.parser.test.symbols to org.junit.platform.commons;
}
