package dk.nikolajbrinch.scanner;

import java.io.IOException;

@FunctionalInterface
public interface CharReaderFactory {

  CharReader createCharReader(ScannerSource source) throws IOException;
}
