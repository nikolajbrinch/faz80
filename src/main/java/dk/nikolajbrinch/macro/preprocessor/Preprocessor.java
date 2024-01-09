package dk.nikolajbrinch.macro.preprocessor;

import dk.nikolajbrinch.macro.parser.MacroParser;
import dk.nikolajbrinch.macro.statements.Statement;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Preprocessor {

  private boolean hasErrors = false;

  public InputStream preprocess(File file) throws IOException {
    return toInputStream(resolveMacros(file));
  }

  private String resolveMacros(File file) throws IOException {
    List<Statement> statements = new MacroParser().parse(file);
    return String.join("", new MacroResolver().resolve(statements));
  }

  private static InputStream toInputStream(String string) {
    return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
  }

  public boolean hasErrors() {
    return hasErrors;
  }
}
