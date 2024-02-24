package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ParameterNode(AssemblerToken name, ExpressionNode defaultValue) implements Node {

  @Override
  public NodeType type() {
    return NodeType.PARAMETER;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitParameterNode(this);
  }
}
