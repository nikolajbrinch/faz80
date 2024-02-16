package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.parser.cst.CstNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;

public interface OperandNode extends CstNode {

  OperandType operandType();

  default NodeType type() {
    return NodeType.OPERAND;
  }
}
