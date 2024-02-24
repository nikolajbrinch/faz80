package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.CompositeLineNode;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;

public record MacroNode(LineNode startLine, CompositeLineNode body, LineNode endLine)
    implements BlockNode {

  @Override
  public BlockType blockType() {
    return BlockType.MACRO;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitMacroNode(this);
  }
}