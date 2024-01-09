package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.parser.Line;

public record ExpressionStatement(Expression expression) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitExpressionStatement(this);
  }

  @Override
  public Line line() {
    return expression.line();
  }
}
