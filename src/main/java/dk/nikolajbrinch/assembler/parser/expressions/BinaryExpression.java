package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.scanner.Token;

public record BinaryExpression(Expression left, Token operator, Expression right) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitBinaryExpression(this);
  }
}
