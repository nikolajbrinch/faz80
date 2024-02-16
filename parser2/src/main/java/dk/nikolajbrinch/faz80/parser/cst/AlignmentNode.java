package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record AlignmentNode(AssemblerToken token, ExpressionNode alignment, ExpressionNode fillByte)
    implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.ALIGNMENT;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitAlignmentNode(this);
  }
}
