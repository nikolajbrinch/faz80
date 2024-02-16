package dk.nikolajbrinch.scanner;

import dk.nikolajbrinch.faz80.scanner.Mode;
import java.io.IOException;

public interface ScannerRegistry<T> {
  void register(ScannerSource source) throws IOException;

  void register(String filename) throws IOException;

  void unregister();

  Scanner<T> getCurrentScanner();

  boolean isBaseScanner();

  void setMode(Mode mode);
}
