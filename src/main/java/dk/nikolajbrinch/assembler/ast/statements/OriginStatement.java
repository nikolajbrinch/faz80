package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.parser.Line;

public record OriginStatement(Expression location, Expression fillByte) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitOriginStatement(this);
  }

  @Override
  public Line line() {
    return location.line();
  }
}
