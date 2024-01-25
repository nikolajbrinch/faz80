package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

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
