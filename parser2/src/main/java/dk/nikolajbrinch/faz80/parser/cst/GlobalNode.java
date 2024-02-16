package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record GlobalNode(AssemblerToken token, AssemblerToken identifier) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.GLOBAL;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitGlobalNode(this);
  }
}
