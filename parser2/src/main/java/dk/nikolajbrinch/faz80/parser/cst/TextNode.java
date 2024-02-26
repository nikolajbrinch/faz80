package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.blocks.BodyNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record TextNode(AssemblerToken text) implements Node, BodyNode {

  @Override
  public NodeType type() {
    return NodeType.TEXT;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitTextNode(this);
  }
}
