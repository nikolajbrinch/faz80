package dk.nikolajbrinch.faz80.parser.cst.conditional;

import dk.nikolajbrinch.faz80.parser.cst.CompositeLineNode;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import java.util.ArrayList;
import java.util.List;

public record ConditionalNode(
    LineNode ifLine,
    CompositeLineNode thenLines,
    LineNode elseLine,
    CompositeLineNode elseLines,
    LineNode elseIfLine)
    implements CompositeLineNode {

  @Override
  public NodeType type() {
    return NodeType.CONDITIONAL;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitConditionalNode(this);
  }

  @Override
  public List<LineNode> lines() {
    List<LineNode> lines = new ArrayList<>();
    lines.add(ifLine);
    lines.addAll(thenLines.lines());
    lines.add(elseLine);
    lines.addAll(elseLines.lines());
    lines.add(elseIfLine);

    return lines;
  }
}
