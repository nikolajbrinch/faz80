package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record UnaryExpressionNode(AssemblerToken operator, ExpressionNode expression)
    implements ExpressionNode {

  @Override
  public ExpressionType expressionType() {
    return ExpressionType.UNARY;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitUnaryExpressionNode(this);
  }
}
