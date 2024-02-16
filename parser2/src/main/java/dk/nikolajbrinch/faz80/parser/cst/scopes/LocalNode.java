package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodesNode;
import dk.nikolajbrinch.faz80.parser.cst.Symbols;

public record LocalNode(
    Symbols symbols, LineNode startDirective, NodesNode nodes, LineNode endDirective)
    implements ScopeNode {

  @Override
  public ScopeType scopeType() {
    return ScopeType.LOCAL;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitLocalNode(this);
  }
}
