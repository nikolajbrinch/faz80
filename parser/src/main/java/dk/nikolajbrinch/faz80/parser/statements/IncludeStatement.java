package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record IncludeStatement(AssemblerToken token, AssemblerToken string) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitIncludeStatement(this);
  }
  @Override
  public SourceInfo sourceInfo() { return string.sourceInfo(); }

  @Override
  public Line line() {
    return string.line();
  }
}
