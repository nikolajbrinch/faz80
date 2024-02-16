package dk.nikolajbrinch.faz80.parser.cst;

public class CstParserConfiguration {
  private final boolean resolveIncludes;

  public CstParserConfiguration() {
    this(true);
  }
  public CstParserConfiguration(boolean resolveIncludes) {
    this.resolveIncludes = resolveIncludes;
  }

  public boolean isResolveIncludes() {
    return resolveIncludes;
  }
}
