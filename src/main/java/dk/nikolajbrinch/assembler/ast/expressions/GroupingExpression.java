package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.parser.Line;

public record GroupingExpression(Expression expression) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitGroupingExpression(this);
  }

  @Override
  public Line line() {
    return expression.line();
  }
}
