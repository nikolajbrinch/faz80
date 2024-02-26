package dk.nikolajbrinch.faz80.parser.cst.conditional;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;

public record ConditionalNode(
    LineNode ifLine, LinesNode thenLines, LineNode elseLine, LinesNode elseLines, LineNode elseIfLine)
    implements LineNode {

  @Override
  public NodeType type() {
    return NodeType.CONDITIONAL;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitConditionalNode(this);
  }

}
