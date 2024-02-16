package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;
import dk.nikolajbrinch.parser.BaseParserConfiguration;

public class AssemblerParserConfiguration extends BaseParserConfiguration<AssemblerTokenType> {

  private boolean ignoreBlankLines = true;

  private boolean resolveIncludes = true;

  public AssemblerParserConfiguration() {
    this(true, true, true);
  }

  public AssemblerParserConfiguration(
      boolean ignoreComments, boolean ignoreBlankLines, boolean resolveIncludes) {
    this(
        ignoreComments,
        ignoreBlankLines,
        resolveIncludes,
        new AssemblerTokenType[] {AssemblerTokenType.EOF, AssemblerTokenType.END},
        new AssemblerTokenType[] {AssemblerTokenType.COMMENT});
  }

  public AssemblerParserConfiguration(
      boolean ignoreComments,
      boolean ignoreBlankLines,
      boolean resolveIncludes,
      AssemblerTokenType[] eofTypes,
      AssemblerTokenType[] commentTypes) {
    super(ignoreComments, eofTypes, commentTypes);
    this.ignoreBlankLines = ignoreBlankLines;
    this.resolveIncludes = resolveIncludes;
  }

  public boolean isIgnoreBlankLines() {
    return ignoreBlankLines;
  }

  public boolean isResolveIncludes() {
    return resolveIncludes;
  }
}
