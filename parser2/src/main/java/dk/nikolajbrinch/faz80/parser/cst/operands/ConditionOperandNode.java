package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ConditionOperandNode(AssemblerToken condition) implements OperandNode {

  @Override
  public OperandType operandType() {
    return OperandType.CONDITION;
  }

}
