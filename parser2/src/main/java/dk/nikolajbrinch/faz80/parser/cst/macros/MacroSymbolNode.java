package dk.nikolajbrinch.faz80.parser.cst.macros;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.TextNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockType;

public record MacroSymbolNode(LineNode start, TextNode body, LineNode end)
    implements BlockNode<TextNode> {

  @Override
  public BlockType blockType() {
    return BlockType.MACRO;
  }

}
