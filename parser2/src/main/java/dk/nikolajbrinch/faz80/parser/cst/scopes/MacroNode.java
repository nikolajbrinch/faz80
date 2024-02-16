package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CstNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodesNode;
import dk.nikolajbrinch.faz80.parser.cst.Symbols;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeType;
import java.util.List;

public record MacroNode(
    Symbols symbols, LineNode startDirective, NodesNode nodes, LineNode endDirective)
    implements ScopeNode {

  @Override
  public ScopeType scopeType() {
    return ScopeType.MACRO;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitMacroNode(this);
  }
}
