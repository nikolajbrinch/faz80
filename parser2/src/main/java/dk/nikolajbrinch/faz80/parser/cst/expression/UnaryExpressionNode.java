package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record UnaryExpressionNode(AssemblerToken operator, ExpressionNode expression)
    implements ExpressionNode {

  @Override
  public ExpressionType expressionType() {
    return ExpressionType.UNARY;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitUnaryExpressionNode(this);
  }
}
