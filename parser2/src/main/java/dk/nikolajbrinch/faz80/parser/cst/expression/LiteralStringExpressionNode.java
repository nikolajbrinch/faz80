package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record LiteralStringExpressionNode(AssemblerToken stringLiteral) implements ExpressionNode {

  @Override
  public ExpressionType expressionType() {
    return ExpressionType.LITERAL;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitLiteralStringExpressionNode(this);
  }
}
