package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record BinaryExpressionNode(
    ExpressionNode left, AssemblerToken operator, ExpressionNode right) implements ExpressionNode {

  @Override
  public ExpressionType expressionType() {
    return ExpressionType.BINARY;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitBinaryExpressionNode(this);
  }
}
