package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;

public interface BlockNode<B extends BodyNode> extends LineNode {
  BlockType blockType();

  LineNode start();

  B body();

  LineNode end();

  @Override
  default NodeType type() {
    return NodeType.BLOCK;
  }
}
