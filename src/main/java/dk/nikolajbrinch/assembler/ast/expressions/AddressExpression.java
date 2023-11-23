package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;

public record AddressExpression(AssemblerToken token) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitAddressExpression(this);
  }
}
