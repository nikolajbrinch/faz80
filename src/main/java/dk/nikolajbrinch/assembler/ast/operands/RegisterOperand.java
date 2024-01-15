package dk.nikolajbrinch.assembler.ast.operands;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.parser.Line;

public record RegisterOperand(Line line, Register register, Expression displacement)
    implements Operand {

  @Override
  public <R> R accept(OperandVisitor<R> visitor) {
    return visitor.visitRegisterOperand(this);
  }
}
