package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record LiteralStringExpressionNode(AssemblerToken stringLiteral) implements ExpressionNode {

  @Override
  public ExpressionType expressionType() {
    return ExpressionType.LITERAL;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitLiteralStringExpressionNode(this);
  }
}
