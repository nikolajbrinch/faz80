package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.parser.Condition;

public record ConditionExpression(Condition condition) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitConditionExpression(this);
  }
}
