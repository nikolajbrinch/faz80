package dk.nikolajbrinch.faz80.parser.cst;


public record ProgramNode(Symbols symbols, NodesNode nodes) implements CstNode {

  @Override
  public NodeType type() {
    return NodeType.PROGRAM;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitProgramNode(this);
  }
}
