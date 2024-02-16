package dk.nikolajbrinch.faz80.parser.cst;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public record NodesNode(List<CstNode> nodes) implements CstNode {

  @Override
  public NodeType type() {
    return NodeType.NDOES;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitNodesNode(this);
  }

  public <R> Stream<R> map(Function<? super CstNode, ? extends R> mapper) {
    return nodes.stream().map(mapper);
  }
}
