package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record EndNode(AssemblerToken token) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.END;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitEndNode(this);
  }
}
