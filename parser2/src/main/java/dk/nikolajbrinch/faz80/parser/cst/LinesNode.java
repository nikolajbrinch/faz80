package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.blocks.BodyNode;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public record LinesNode(List<LineNode> lines) implements LineNode, BodyNode {

  @Override
  public NodeType type() {
    return NodeType.LINES;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitLinesNode(this);
  }

  public <R> Stream<R> map(Function<? super LineNode, ? extends R> mapper) {
    return lines.stream().map(mapper);
  }
}
