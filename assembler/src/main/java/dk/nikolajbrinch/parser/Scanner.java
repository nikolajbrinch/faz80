package dk.nikolajbrinch.parser;

public interface Scanner<T> {

  T peek();

  T peek(int position);

  T next();

  SourceInfo getSourceInfo();
}
