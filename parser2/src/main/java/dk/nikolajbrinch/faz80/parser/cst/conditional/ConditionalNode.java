package dk.nikolajbrinch.faz80.parser.cst.conditional;

import dk.nikolajbrinch.faz80.parser.cst.CstNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.NodesNode;

public record ConditionalNode(
    CstNode ifDirective,
    NodesNode thenBranch,
    CstNode elseDirective,
    NodesNode elseBranch,
    CstNode endIfDirective)
    implements CstNode {

  @Override
  public NodeType type() {
    return NodeType.CONDITIONAL;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitConditionalNode(this);
  }
}
