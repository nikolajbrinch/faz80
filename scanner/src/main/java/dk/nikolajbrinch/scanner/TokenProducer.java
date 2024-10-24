package dk.nikolajbrinch.scanner;

import dk.nikolajbrinch.faz80.scanner.Mode;
import java.io.IOException;

/**
 * Produces tokens from a source
 *
 * @param <T> The type of token to produce
 */
public interface TokenProducer<T> extends Scanner<T> {

  void newSource(ScannerSource source) throws IOException;

  void newSource(String filename) throws IOException;

}
