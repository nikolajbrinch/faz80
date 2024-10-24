package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record GroupingOperandNode(
    AssemblerToken groupStart, OperandNode operand, AssemblerToken groupEnd)
    implements OperandNode {

  @Override
  public OperandType operandType() {
    return OperandType.GROUPING;
  }

}
