package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record LabelNode(AssemblerToken label) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.LABEL;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitLabelNode(this);
  }
}
