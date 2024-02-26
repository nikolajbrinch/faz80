package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.symbols.Symbols;

public record LocalNode(Symbols symbols, LineNode start, LinesNode body, LineNode end)
    implements ScopeNode<LinesNode> {

  @Override
  public ScopeType scopeType() {
    return ScopeType.LOCAL;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitLocalNode(this);
  }
}
