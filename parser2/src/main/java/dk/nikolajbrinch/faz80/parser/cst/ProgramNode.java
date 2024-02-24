package dk.nikolajbrinch.faz80.parser.cst;


import dk.nikolajbrinch.faz80.parser.cst.symbols.Symbols;

public record ProgramNode(Symbols symbols, CompositeLineNode node) implements Node {

  @Override
  public NodeType type() {
    return NodeType.PROGRAM;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitProgramNode(this);
  }
}
