package dk.nikolajbrinch.parser;

import java.io.File;
import java.io.IOException;

public interface TokenProducer<T> extends Scanner<T> {

  void newFile(File file) throws IOException;

  void newFile(String filename) throws IOException;
}
