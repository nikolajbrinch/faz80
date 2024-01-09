package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record UnaryExpression(AssemblerToken operator, Expression expression)
    implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitUnaryExpression(this);
  }

  @Override
  public Line line() {
    return operator.line();
  }
}
