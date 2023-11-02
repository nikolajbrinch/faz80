package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.scanner.Token;

public record IdentifierExpression(Token token) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitIdentifierExpression(this);
  }
}
