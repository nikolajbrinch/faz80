package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record OriginStatement(Expression location, Expression fillByte) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitOriginStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return location.sourceInfo(); }

  @Override
  public Line line() {
    return location.line();
  }
}
