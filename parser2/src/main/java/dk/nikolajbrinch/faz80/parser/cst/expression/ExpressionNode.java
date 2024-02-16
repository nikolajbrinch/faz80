package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.CstNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;

public interface ExpressionNode extends CstNode {

  ExpressionType expressionType();

  @Override
  default NodeType type() {
    return NodeType.EXPRESSION;
  }
}
