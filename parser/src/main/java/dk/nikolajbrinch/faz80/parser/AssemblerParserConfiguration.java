package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.parser.BaseParserConfiguration;

public class AssemblerParserConfiguration extends BaseParserConfiguration {

  private boolean resolveIncludes = true;


  public AssemblerParserConfiguration() {
    super();
  }

  public AssemblerParserConfiguration(boolean ignoreComments, boolean resolveIncludes) {
    super(ignoreComments);
    this.resolveIncludes = resolveIncludes;
  }

  public boolean isResolveIncludes() {
    return resolveIncludes;
  }
}
