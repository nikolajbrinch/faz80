package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record CommentNode(AssemblerToken comment) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.COMMENT;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitCommentNode(this);
  }
}
