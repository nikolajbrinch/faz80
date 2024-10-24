package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record SectionStatement(AssemblerToken token, AssemblerToken name) implements Statement {

  @Override
  public SourceInfo sourceInfo() { return name.sourceInfo(); }

  @Override
  public Line line() {
    return name.line();
  }
}
