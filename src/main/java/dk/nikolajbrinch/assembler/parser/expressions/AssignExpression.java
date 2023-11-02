package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.scanner.Token;

public record AssignExpression(Token identifier, Expression expression) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitAssignExpression(this);
  }
}
