package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record EmptyStatement(SourceInfo sourceInfo, Line line) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitEmptyStatement(this);
  }
}
