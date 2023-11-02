package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;

public record PhaseStatement(Expression expression, Statement block) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitPhaseStatement(this);
  }

}
