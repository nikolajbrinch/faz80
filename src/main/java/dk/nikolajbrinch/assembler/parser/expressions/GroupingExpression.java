package dk.nikolajbrinch.assembler.parser.expressions;

public record GroupingExpression(Expression expression) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitGroupingExpression(this);
  }
}
