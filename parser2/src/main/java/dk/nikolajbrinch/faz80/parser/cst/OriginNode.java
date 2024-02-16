package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record OriginNode(AssemblerToken token, ExpressionNode location, ExpressionNode fillByte)
    implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.ORIGIN;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitOriginNode(this);
  }
}
