package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;

public sealed interface ExpressionNode extends Node
    permits AddressReferenceExpressionNode,
        BinaryExpressionNode,
        GroupingExpressionNode,
        IdentifierExpressionNode,
        LiteralNumberExpressionNode,
        LiteralStringExpressionNode,
        UnaryExpressionNode {

  ExpressionType expressionType();

  @Override
  default NodeType type() {
    return NodeType.EXPRESSION;
  }
}
