package dk.nikolajbrinch.faz80.ide;

import dk.nikolajbrinch.faz80.parser.AssemblerParseResult;
import dk.nikolajbrinch.faz80.parser.AssemblerParser;
import dk.nikolajbrinch.faz80.parser.AssemblerParserConfiguration;
import java.io.IOException;

public class Formatter {

  public String format(String text) throws IOException {
    AssemblerParseResult parseResult = parse(text);

    return text;
  }

  private AssemblerParseResult parse(String text) throws IOException {
    return newParser().parse(text);
  }

  private AssemblerParser newParser() {
    return new AssemblerParser(new AssemblerParserConfiguration(false, false));
  }
}
