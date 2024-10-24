package dk.nikolajbrinch.faz80.parser.expressions;

public interface ExpressionProcessor<R> {

  default R process(Expression expression) {
    return expression == null ? null : switch (expression) {
      case BinaryExpression e -> processBinaryExpression(e);
      case UnaryExpression e -> processUnaryExpression(e);
      case GroupingExpression e -> processGroupingExpression(e);
      case IdentifierExpression e -> processIdentifierExpression(e);
      case AddressExpression e -> processAddressExpression(e);
      case MacroCallExpression e -> processMacroCallExpression(e);
      case NumberExpression e -> processNumberExpression(e);
      case StringExpression e -> processStringExpression(e);
    };
  }

  R processBinaryExpression(BinaryExpression expression);

  R processUnaryExpression(UnaryExpression expression);

  R processGroupingExpression(GroupingExpression expression);

  R processIdentifierExpression(IdentifierExpression expression);

  R processAddressExpression(AddressExpression expression);

  R processMacroCallExpression(MacroCallExpression expression);

  R processNumberExpression(NumberExpression expression);

  R processStringExpression(StringExpression expression);
}
