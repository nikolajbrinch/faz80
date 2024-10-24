package dk.nikolajbrinch.faz80.parser.cst;


import dk.nikolajbrinch.faz80.parser.cst.symbols.Symbols;

public record ProgramNode(Symbols symbols, LinesNode lines) implements Node {

  @Override
  public NodeType type() {
    return NodeType.PROGRAM;
  }

}
