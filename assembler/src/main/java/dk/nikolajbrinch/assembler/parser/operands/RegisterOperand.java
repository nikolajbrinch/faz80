package dk.nikolajbrinch.assembler.parser.operands;

import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;

public record RegisterOperand(Line line, Register register, Expression displacement)
    implements Operand {

  @Override
  public <R> R accept(OperandVisitor<R> visitor) {
    return visitor.visitRegisterOperand(this);
  }
}
