package dk.nikolajbrinch.faz80.parser.cst.conditional;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;

public record ConditionalNode(
    LineNode ifLine, LinesNode thenLines, LineNode elseLine, LinesNode elseLines, LineNode elseIfLine)
    implements LineNode {

  @Override
  public NodeType type() {
    return NodeType.CONDITIONAL;
  }

}
