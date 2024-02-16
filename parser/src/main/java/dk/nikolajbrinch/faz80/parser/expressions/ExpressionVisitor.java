package dk.nikolajbrinch.faz80.parser.expressions;

public interface ExpressionVisitor<R> {

  R visitBinaryExpression(BinaryExpression expression);

  R visitUnaryExpression(UnaryExpression expression);

  R visitGroupingExpression(GroupingExpression expression);

  R visitIdentifierExpression(IdentifierExpression expression);

  R visitAddressExpression(AddressExpression expression);

  R visitMacroCallExpression(MacroCallExpression expression);

  R visitNumberExpression(NumberExpression expression);

  R visitStringExpression(StringExpression expression);
}
