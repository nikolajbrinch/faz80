package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import java.io.File;
import java.io.IOException;

public interface Parser<S> {

  BlockStatement parse(File file) throws IOException;

  default BlockStatement parse(String filename) throws IOException {
    return parse(new File(filename));
  }
}
