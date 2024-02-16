package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CommandNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record RepeatEndNode(AssemblerToken token) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.REPEAT_END;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitRepeatEndNode(this);
  }
}
