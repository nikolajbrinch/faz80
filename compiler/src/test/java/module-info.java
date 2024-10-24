module dk.nikolajbrinch.faz80.compiler.test {
  requires dk.nikolajbrinch.faz80.compiler;
  requires org.junit.jupiter.api;

  opens dk.nikolajbrinch.faz80.compiler.test to org.junit.platform.commons;
}
