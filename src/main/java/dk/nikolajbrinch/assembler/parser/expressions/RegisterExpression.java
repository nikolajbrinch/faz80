package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.parser.Register;

public record RegisterExpression(Register register) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitRegisterExpression(this);
  }
}
