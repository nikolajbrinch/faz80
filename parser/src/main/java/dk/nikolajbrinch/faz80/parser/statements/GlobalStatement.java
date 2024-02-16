package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record GlobalStatement(AssemblerToken token, AssemblerToken identifier) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitGlobalStatement(this);
  }
  @Override
  public SourceInfo sourceInfo() { return identifier.sourceInfo(); }
  @Override
  public Line line() {
    return identifier.line();
  }
}
