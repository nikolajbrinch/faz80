package dk.nikolajbrinch.faz80.parser.expressions;

import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record GroupingExpression(Expression expression) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitGroupingExpression(this);
  }

  @Override
  public SourceInfo sourceInfo() { return expression.sourceInfo(); }

  @Override
  public Line line() {
    return expression.line();
  }
}
