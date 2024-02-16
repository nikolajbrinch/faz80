package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record CommentNode(AssemblerToken comment) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.COMMENT;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitCommentNode(this);
  }
}
