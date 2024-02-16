package dk.nikolajbrinch.scanner;

import dk.nikolajbrinch.faz80.scanner.Mode;

public interface Scanner<T> {

  T peek();

  T peek(int position);

  T next();

  SourceInfo getSourceInfo();

  void setMode(Mode mode);

}
