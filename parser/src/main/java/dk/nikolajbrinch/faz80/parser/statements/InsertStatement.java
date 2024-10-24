package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record InsertStatement(AssemblerToken token, AssemblerToken string) implements Statement {

  @Override
  public SourceInfo sourceInfo() { return string.sourceInfo(); }

  @Override
  public Line line() {
    return string.line();
  }
}
