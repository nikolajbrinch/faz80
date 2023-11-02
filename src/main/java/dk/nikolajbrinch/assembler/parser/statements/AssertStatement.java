package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;

public record AssertStatement(Expression expression) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitAssertStatement(this);
  }
}
