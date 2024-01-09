package dk.nikolajbrinch.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Parser<S> {

  List<S> parse(File file) throws IOException;

  default List<S> parse(String filename) throws IOException {
    return parse(new File(filename));
  }
}
