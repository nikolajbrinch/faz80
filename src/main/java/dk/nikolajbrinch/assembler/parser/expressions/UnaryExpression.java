package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public record UnaryExpression(AssemblerToken operator, Expression expression)
    implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitUnaryExpression(this);
  }

  @Override
  public SourceInfo sourceInfo() { return operator.sourceInfo(); }

  @Override
  public Line line() {
    return operator.line();
  }
}
