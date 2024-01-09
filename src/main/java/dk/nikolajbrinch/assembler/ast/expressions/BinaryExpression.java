package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record BinaryExpression(Expression left, AssemblerToken operator, Expression right)
    implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitBinaryExpression(this);
  }

  @Override
  public Line line() {
    return left.line();
  }
}
