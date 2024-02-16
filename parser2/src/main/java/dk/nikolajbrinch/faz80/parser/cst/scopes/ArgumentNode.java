package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CstNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record ArgumentNode(AssemblerToken boundsStart, List<AssemblerToken> tokens, AssemblerToken boundsEnd) implements CstNode {

  @Override
  public NodeType type() {
    return NodeType.ARGUMENT;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitArgumentNode(this);
  }

}
