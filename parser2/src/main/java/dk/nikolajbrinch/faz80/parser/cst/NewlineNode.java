package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record NewlineNode(AssemblerToken newline) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.NEWLINE;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitNewlineNode(this);
  }
}
