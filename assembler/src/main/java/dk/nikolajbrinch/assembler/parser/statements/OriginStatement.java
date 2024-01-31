package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

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
