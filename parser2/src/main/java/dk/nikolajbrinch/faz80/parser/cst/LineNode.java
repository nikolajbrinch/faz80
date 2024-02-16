package dk.nikolajbrinch.faz80.parser.cst;


public record LineNode(
    LabelNode label, CommandNode command, CommentNode comment, NewlineNode newline)
    implements CstNode {

  @Override
  public NodeType type() {
    return NodeType.LINE;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitLineNode(this);
  }
}
