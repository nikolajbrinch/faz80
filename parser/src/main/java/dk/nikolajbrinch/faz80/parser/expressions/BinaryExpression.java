package dk.nikolajbrinch.faz80.parser.expressions;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record BinaryExpression(Expression left, AssemblerToken operator, Expression right)
    implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitBinaryExpression(this);
  }

  @Override
  public SourceInfo sourceInfo() { return left.sourceInfo(); }

  @Override
  public Line line() {
    return left.line();
  }
}
