package dk.nikolajbrinch.faz80.parser.expressions;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record AddressExpression(AssemblerToken token) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitAddressExpression(this);
  }

  @Override
  public SourceInfo sourceInfo() { return token.sourceInfo(); }

  @Override
  public Line line() {
    return token.line();
  }
}
