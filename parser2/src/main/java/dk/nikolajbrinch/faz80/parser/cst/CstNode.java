package dk.nikolajbrinch.faz80.parser.cst;


public interface CstNode {

  NodeType type();

  <R> R accept(CstVisitor<R> visitor);

}
