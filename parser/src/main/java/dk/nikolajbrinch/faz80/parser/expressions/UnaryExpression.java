package dk.nikolajbrinch.faz80.parser.expressions;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record UnaryExpression(AssemblerToken operator, Expression expression)
    implements Expression {

  @Override
  public SourceInfo sourceInfo() { return operator.sourceInfo(); }

  @Override
  public Line line() {
    return operator.line();
  }
}
