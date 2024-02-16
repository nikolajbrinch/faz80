package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.operands.OperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.OperandType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ConditionOperandNode(AssemblerToken condition) implements OperandNode {

  @Override
  public OperandType operandType() {
    return OperandType.CONDITION;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitConditionOperandNode(this);
  }
}
