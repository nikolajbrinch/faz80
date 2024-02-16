package dk.nikolajbrinch.faz80.parser.cst.operands;

import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
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
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitRegisterOperandNode(this);
  }
}
