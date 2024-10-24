module dk.nikolajbrinch.faz80.scanner.test {
  requires dk.nikolajbrinch.faz80.scanner;
  requires org.junit.jupiter.api;

  opens dk.nikolajbrinch.faz80.scanner.test to org.junit.platform.commons;
  opens dk.nikolajbrinch.faz80.scanner.test.impl to org.junit.platform.commons;
}
