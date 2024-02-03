package dk.nikolajbrinch.faz80.parser.expressions;

import dk.nikolajbrinch.faz80.parser.values.NumberValue;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record NumberExpression(AssemblerToken token, NumberValue numberValue) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitNumberExpression(this);
  }

  @Override
  public SourceInfo sourceInfo() { return token.sourceInfo(); }

  @Override
  public Line line() {
    return token.line();
  }
}
