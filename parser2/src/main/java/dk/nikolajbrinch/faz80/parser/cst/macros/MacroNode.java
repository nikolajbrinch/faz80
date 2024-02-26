package dk.nikolajbrinch.faz80.parser.cst.macros;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.TextNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockType;

public record MacroNode(LineNode start, LinesNode body, LineNode end)
    implements BlockNode<LinesNode> {

  @Override
  public BlockType blockType() {
    return BlockType.MACRO;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitMacroNode(this);
  }
}
