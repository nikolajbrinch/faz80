package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record IdentifierExpressionNode(AssemblerToken identifier) implements ExpressionNode {

  @Override
  public ExpressionType expressionType() {
    return ExpressionType.IDENTIFIER;
  }

}
