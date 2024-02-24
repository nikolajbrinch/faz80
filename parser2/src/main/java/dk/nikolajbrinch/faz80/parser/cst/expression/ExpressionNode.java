package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;

public interface ExpressionNode extends Node {

  ExpressionType expressionType();

  @Override
  default NodeType type() {
    return NodeType.EXPRESSION;
  }
}
