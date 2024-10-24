package dk.nikolajbrinch.faz80.formatter;

import dk.nikolajbrinch.faz80.parser.cst.BasicLineNode;
import dk.nikolajbrinch.faz80.parser.cst.LabelNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeProcessorAdapter;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ConditionalNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;

public class LabelLengthFinder implements NodeProcessorAdapter<Integer> {

  public Integer findLabelLength(ProgramNode programNode) {
    return process(programNode);
  }

  @Override
  public Integer processProgramNode(ProgramNode node) {
    return process(node.lines());
  }

  @Override
  public Integer processScopeNode(ScopeNode<? extends Node> node) {
    return max(Stream.of(node.start(), node.body(), node.end()));
  }

  @Override
  public Integer processConditionalNode(ConditionalNode node) {
    return max(
        Stream.of(
            node.ifLine(), node.thenLines(), node.elseLine(), node.elseLines(), node.elseIfLine()));
  }

  @Override
  public Integer processBasicLineNode(BasicLineNode node) {
    LabelNode label = node.label();

    return label != null ? label.label().text().length() : -1;
  }

  @Override
  public Integer processLinesNode(LinesNode node) {
    return max(node.lines().stream());
  }

  private Integer max(Stream<? extends Node> nodeStream) {
    OptionalInt optional =
        nodeStream
            .filter(Objects::nonNull)
            .map(this::process)
            .filter(Objects::nonNull)
            .mapToInt(i -> i)
            .max();

    return optional.isEmpty() ? null : optional.getAsInt();
  }
}
