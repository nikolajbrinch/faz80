package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.parser.AssemblerParseResult;
import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import dk.nikolajbrinch.assembler.parser.AssemblerParserConfiguration;
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
