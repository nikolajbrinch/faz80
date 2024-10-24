package dk.nikolajbrinch.faz80.parser.operands;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.scanner.Line;

public record ExpressionOperand(Expression expression) implements Operand {

  @Override
  public Line line() {
    return expression.line();
  }
}
