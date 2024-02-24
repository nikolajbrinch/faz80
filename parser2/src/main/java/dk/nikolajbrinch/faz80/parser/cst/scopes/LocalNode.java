package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CompositeLineNode;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.symbols.Symbols;

public record LocalNode(
    Symbols symbols, LineNode startLine, CompositeLineNode body, LineNode endLine)
    implements ScopeNode {

  @Override
  public ScopeType scopeType() {
    return ScopeType.LOCAL;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitLocalNode(this);
  }
}
