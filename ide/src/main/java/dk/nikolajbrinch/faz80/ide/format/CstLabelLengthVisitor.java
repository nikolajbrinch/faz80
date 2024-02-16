package dk.nikolajbrinch.faz80.ide.format;

import dk.nikolajbrinch.faz80.parser.cst.CommentNode;
import dk.nikolajbrinch.faz80.parser.cst.CstNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitorAdapter;
import dk.nikolajbrinch.faz80.parser.cst.LabelNode;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NewlineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodesNode;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ConditionalNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;

public class CstLabelLengthVisitor implements CstVisitorAdapter<Integer> {

  @Override
  public Integer visitProgramNode(ProgramNode node) {
    return node.nodes().accept(this);
  }

  @Override
  public Integer visitScopeNode(ScopeNode node) {
    return max(Stream.of(node.startDirective(), node.nodes(), node.endDirective()));
  }

  @Override
  public Integer visitConditionalNode(ConditionalNode node) {
    return max(
        Stream.of(
            node.ifDirective(),
            node.thenBranch(),
            node.elseDirective(),
            node.elseBranch(),
            node.endIfDirective()));
  }

  @Override
  public Integer visitNodesNode(NodesNode nodes) {
    return max(nodes.nodes().stream());
  }

  @Override
  public Integer visitLineNode(LineNode node) {
    LabelNode label = node.label();

    return label != null ? label.label().text().length() : -1;
  }

  private Integer max(Stream<CstNode> nodeStream) {
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
