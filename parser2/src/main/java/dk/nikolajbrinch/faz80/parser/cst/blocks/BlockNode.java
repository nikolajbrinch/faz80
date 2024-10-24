package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroSymbolNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;

public sealed interface BlockNode<B extends BodyNode> extends LineNode
    permits PhaseNode, RepeatNode, MacroNode, MacroSymbolNode, ScopeNode {
  BlockType blockType();

  LineNode start();

  B body();

  LineNode end();

  @Override
  default NodeType type() {
    return NodeType.BLOCK;
  }
}
