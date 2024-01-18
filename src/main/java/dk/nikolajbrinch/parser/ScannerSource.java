package dk.nikolajbrinch.parser;

import java.io.IOException;
import java.io.InputStream;

public interface ScannerSource {

  SourceInfo getSourceInfo();

  InputStream openStream() throws IOException;

}
