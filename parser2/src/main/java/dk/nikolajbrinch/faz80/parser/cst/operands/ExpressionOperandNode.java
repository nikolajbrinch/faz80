package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;

public record ExpressionOperandNode(ExpressionNode expression) implements OperandNode {

  @Override
  public OperandType operandType() {
    return OperandType.EXPRESSION;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitExpressionOperandNode(this);
  }
}
