package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;

public record BasicLineNode(
    LabelNode label, InstructionNode instruction, CommentNode comment, NewlineNode newline)
    implements LineNode {

  public BasicLineNode(InstructionNode instruction, CommentNode comment, NewlineNode newline) {
    this(null, instruction, comment, newline);
  }

  @Override
  public NodeType type() {
    return NodeType.SINGLE_LINE;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitSingleLineNode(this);
  }
}
