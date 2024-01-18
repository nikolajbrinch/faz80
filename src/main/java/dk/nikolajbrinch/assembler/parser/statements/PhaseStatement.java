package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;

public record PhaseStatement(Expression expression, Statement block) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitPhaseStatement(this);
  }

  @Override
  public Line line() {
    return expression.line();
  }
}
