package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record GroupingExpressionNode(
    AssemblerToken groupStart, ExpressionNode expression, AssemblerToken groupEnd)
    implements ExpressionNode {

  @Override
  public ExpressionType expressionType() {
    return ExpressionType.GROUPING;
  }

}
