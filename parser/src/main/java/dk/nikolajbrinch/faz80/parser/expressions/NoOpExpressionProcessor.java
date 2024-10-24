package dk.nikolajbrinch.faz80.parser.expressions;

public interface NoOpExpressionProcessor extends ExpressionProcessor<Expression> {

  @Override
  default Expression processBinaryExpression(BinaryExpression expression) {
    return expression;
  }

  @Override
  default Expression processUnaryExpression(UnaryExpression expression) {
    return expression;
  }

  @Override
  default Expression processGroupingExpression(GroupingExpression expression) {
    return expression;
  }

  @Override
  default Expression processIdentifierExpression(IdentifierExpression expression) {
    return expression;
  }

  @Override
  default Expression processAddressExpression(AddressExpression expression) {
    return expression;
  }

  @Override
  default Expression processMacroCallExpression(MacroCallExpression expression) {
    return expression;
  }

  @Override
  default Expression processNumberExpression(NumberExpression expression) {
    return expression;
  }

  @Override
  default Expression processStringExpression(StringExpression expression) {
    return expression;
  }
}
