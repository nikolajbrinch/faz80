package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record EmptyStatement(AssemblerToken eol) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitEmptyStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() {
    return eol.sourceInfo();
  }

  @Override
  public Line line() {
    return eol.line();
  }
}
