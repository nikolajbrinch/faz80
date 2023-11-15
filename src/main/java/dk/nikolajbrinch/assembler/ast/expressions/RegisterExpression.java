package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.parser.Register;

public record RegisterExpression(Register register, Expression displacement) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitRegisterExpression(this);
  }
}
