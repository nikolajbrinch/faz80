package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record RegisterOperandNode(
    AssemblerToken register, AssemblerToken operator, ExpressionNode displacement)
    implements OperandNode {

  @Override
  public OperandType operandType() {
    return OperandType.REGISTER;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitRegisterOperandNode(this);
  }
}
