package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;

public record AssignExpression(AssemblerToken identifier, Expression expression)
    implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitAssignExpression(this);
  }
}
