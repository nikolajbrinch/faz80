package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record AssertionNode(AssemblerToken token, ExpressionNode expression)
    implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.ASSERTION;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitAssertionNode(this);
  }
}
