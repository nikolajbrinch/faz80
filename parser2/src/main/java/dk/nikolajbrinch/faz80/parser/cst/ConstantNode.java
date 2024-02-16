package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ConstantNode(AssemblerToken operator, ExpressionNode expression)
    implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.CONSTANT;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitConstantNode(this);
  }
}
