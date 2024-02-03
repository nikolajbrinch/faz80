package dk.nikolajbrinch.scanner;

public interface Scanner<T> {

  T peek();

  T peek(int position);

  T next();

  SourceInfo getSourceInfo();
}
