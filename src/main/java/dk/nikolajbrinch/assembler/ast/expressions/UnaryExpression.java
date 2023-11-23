package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;

public record UnaryExpression(AssemblerToken operator, Expression expression)
    implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitUnaryExpression(this);
  }
}
