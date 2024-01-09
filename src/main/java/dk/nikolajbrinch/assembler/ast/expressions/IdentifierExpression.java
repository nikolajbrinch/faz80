package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record IdentifierExpression(AssemblerToken token) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitIdentifierExpression(this);
  }

  @Override
  public Line line() {
    return token.line();
  }
}
