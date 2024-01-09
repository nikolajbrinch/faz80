package dk.nikolajbrinch.parser;

import java.io.File;
import java.io.IOException;

public interface ScannerRegistry<T> {
  void register(File file) throws IOException;

  void register(String filename) throws IOException;

  void unregister();

  Scanner<T> getCurrentScanner();

  boolean isBaseScanner();
}
