package dk.nikolajbrinch.faz80.parser.cst;


public interface Node {

  NodeType type();

  <R> R accept(NodeVisitor<R> visitor);

}
