package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CstNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.NodesNode;
import dk.nikolajbrinch.faz80.parser.cst.Symbols;

public interface ScopeNode extends CstNode {
  ScopeType scopeType();

  Symbols symbols();

  LineNode startDirective();

  NodesNode nodes();

  LineNode endDirective();

  @Override
  default NodeType type() {
    return NodeType.SCOPE;
  }

}
