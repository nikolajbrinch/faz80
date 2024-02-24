package dk.nikolajbrinch.faz80.parser.cst;

public class ParserConfiguration {
  private final boolean resolveIncludes;

  private final boolean expandMacros;

  public ParserConfiguration() {
    this(true, true);
  }
  public ParserConfiguration(boolean resolveIncludes, boolean expandMacros) {
    this.resolveIncludes = resolveIncludes;
    this.expandMacros = expandMacros;
  }

  public boolean isResolveIncludes() {
    return resolveIncludes;
  }

  public boolean isExpandMacros() {
    return expandMacros;
  }

  ;
}
