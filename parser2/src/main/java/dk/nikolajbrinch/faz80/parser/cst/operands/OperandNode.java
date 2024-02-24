package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;

public interface OperandNode extends Node {

  OperandType operandType();

  default NodeType type() {
    return NodeType.OPERAND;
  }
}
