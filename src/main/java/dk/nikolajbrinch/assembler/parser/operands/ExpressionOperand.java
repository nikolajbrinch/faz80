package dk.nikolajbrinch.assembler.parser.operands;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;

public record ExpressionOperand(Expression expression) implements Operand {

  @Override
  public <R> R accept(OperandVisitor<R> visitor) {
    return visitor.visitExpressionOperand(this);
  }

  @Override
  public Line line() {
    return expression.line();
  }
}
