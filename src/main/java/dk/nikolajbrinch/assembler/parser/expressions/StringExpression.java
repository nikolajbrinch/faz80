package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.compiler.values.StringValue;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record StringExpression(AssemblerToken token, StringValue stringValue) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitStringExpression(this);
  }

  @Override
  public Line line() {
    return token.line();
  }
}
