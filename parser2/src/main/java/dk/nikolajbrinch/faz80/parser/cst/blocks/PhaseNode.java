package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;

public record PhaseNode(LineNode start, LinesNode body, LineNode end)
    implements BlockNode<LinesNode> {

  @Override
  public BlockType blockType() {
    return BlockType.PHASE;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitPhaseNode(this);
  }
}
