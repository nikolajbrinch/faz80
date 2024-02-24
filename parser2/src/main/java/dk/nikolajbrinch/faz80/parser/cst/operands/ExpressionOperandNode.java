package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;

public record ExpressionOperandNode(ExpressionNode expression) implements OperandNode {

  @Override
  public OperandType operandType() {
    return OperandType.EXPRESSION;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitExpressionOperandNode(this);
  }
}
