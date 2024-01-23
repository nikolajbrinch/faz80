package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record NumberExpression(AssemblerToken token, NumberValue numberValue) implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitNumberExpression(this);
  }

  @Override
  public Line line() {
    return token.line();
  }
}
