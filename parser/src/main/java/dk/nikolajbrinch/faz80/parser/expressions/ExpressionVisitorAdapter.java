package dk.nikolajbrinch.faz80.parser.expressions;

public interface ExpressionVisitorAdapter<R> extends ExpressionVisitor<R> {

  @Override
  default R visitBinaryExpression(BinaryExpression expression) {
    return null;
  }

  @Override
  default R visitUnaryExpression(UnaryExpression expression) {
    return null;
  }

  @Override
  default R visitGroupingExpression(GroupingExpression expression) {
    return null;
  }

  @Override
  default R visitIdentifierExpression(IdentifierExpression expression) {
    return null;
  }

  @Override
  default R visitAddressExpression(AddressExpression expression) {
    return null;
  }

  @Override
  default R visitMacroCallExpression(MacroCallExpression expression) {
    return null;
  }

  @Override
  default R visitNumberExpression(NumberExpression expression) {
    return null;
  }

  @Override
  default R visitStringExpression(StringExpression expression) {
    return null;
  }
}
