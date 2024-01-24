package dk.nikolajbrinch.parser;

import java.io.IOException;

public interface ScannerRegistry<T> {
  void register(ScannerSource source) throws IOException;

  void register(String filename) throws IOException;

  void unregister();

  Scanner<T> getCurrentScanner();

  boolean isBaseScanner();
}
