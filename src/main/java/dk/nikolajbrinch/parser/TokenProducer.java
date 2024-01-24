package dk.nikolajbrinch.parser;

import java.io.IOException;

public interface TokenProducer<T> extends Scanner<T> {
  void newSource(ScannerSource source) throws IOException;

  void newSource(String filename) throws IOException;
}
