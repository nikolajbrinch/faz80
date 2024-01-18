package dk.nikolajbrinch.assembler.parser.operands;

import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.parser.Line;

public record ConditionOperand(Line line, Condition condition) implements Operand {

  @Override
  public <R> R accept(OperandVisitor<R> visitor) {
    return visitor.visitConditionOperand(this);
  }
}
