package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;

public sealed interface OperandNode extends Node
    permits ConditionOperandNode, ExpressionOperandNode, GroupingOperandNode, RegisterOperandNode {

  OperandType operandType();

  default NodeType type() {
    return NodeType.OPERAND;
  }
}
