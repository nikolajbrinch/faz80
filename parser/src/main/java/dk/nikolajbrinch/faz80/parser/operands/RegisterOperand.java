package dk.nikolajbrinch.faz80.parser.operands;

import dk.nikolajbrinch.faz80.parser.Register;
import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.scanner.Line;

public record RegisterOperand(Line line, Register register, Expression displacement)
    implements Operand {

  @Override
  public <R> R accept(OperandVisitor<R> visitor) {
    return visitor.visitRegisterOperand(this);
  }
}
