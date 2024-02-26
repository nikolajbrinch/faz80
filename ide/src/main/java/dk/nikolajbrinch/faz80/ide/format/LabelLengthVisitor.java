package dk.nikolajbrinch.faz80.ide.format;

import dk.nikolajbrinch.faz80.parser.cst.BasicLineNode;
import dk.nikolajbrinch.faz80.parser.cst.LabelNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitorAdapter;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ConditionalNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;

public class LabelLengthVisitor implements NodeVisitorAdapter<Integer> {

  @Override
  public Integer visitProgramNode(ProgramNode node) {
    return node.lines().accept(this);
  }

  @Override
  public Integer visitScopeNode(ScopeNode<? extends Node> node) {
    return max(Stream.of(node.start(), node.body(), node.end()));
  }

  @Override
  public Integer visitConditionalNode(ConditionalNode node) {
    return max(
        Stream.of(
            node.ifLine(),
            node.thenLines(),
            node.elseLine(),
            node.elseLines(),
            node.elseIfLine()));
  }

  @Override
  public Integer visitSingleLineNode(BasicLineNode node) {
    LabelNode label = node.label();

    return label != null ? label.label().text().length() : -1;
  }

  @Override
  public Integer visitLinesNode(LinesNode node) {
    return max(node.lines().stream());
  }

  private Integer max(Stream<? extends Node> nodeStream) {
    OptionalInt optional =
        nodeStream
            .filter(Objects::nonNull)
            .map(node -> node.accept(this))
            .filter(Objects::nonNull)
            .mapToInt(i -> i)
            .max();

    return optional.isEmpty() ? null : optional.getAsInt();
  }
}
