package dk.nikolajbrinch.faz80.parser.cst.conditional;

import dk.nikolajbrinch.faz80.parser.cst.CommandNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ElseNode(AssemblerToken token) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.ELSE;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitElseNode(this);
  }
}
