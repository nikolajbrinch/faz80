package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CstNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ParameterNode(AssemblerToken name, ExpressionNode defaultValue) implements CstNode {

  @Override
  public NodeType type() {
    return NodeType.PARAMETER;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitParameterNode(this);
  }
}
