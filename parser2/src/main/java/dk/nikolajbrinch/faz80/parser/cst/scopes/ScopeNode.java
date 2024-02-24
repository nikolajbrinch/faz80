package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockType;
import dk.nikolajbrinch.faz80.parser.cst.symbols.Symbols;

public interface ScopeNode extends BlockNode {
  ScopeType scopeType();

  Symbols symbols();

  @Override
  default BlockType blockType() {
    return BlockType.SCOPE;
  }

}
