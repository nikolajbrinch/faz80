package dk.nikolajbrinch.assembler.parser.expressions;

public interface ExpressionVisitor<R> {

  R visitBinaryExpression(BinaryExpression expression);

  R visitUnaryExpression(UnaryExpression expression);

  R visitGroupingExpression(GroupingExpression expression);

  R visitIdentifierExpression(IdentifierExpression expression);

  R visitAddressExpression(AddressExpression addressExpression);

  R visitMacroCallExpression(MacroCallExpression macroCallExpression);

  R visitNumberExpression(NumberExpression numberExpression);

  R visitStringExpression(StringExpression stringExpression);
}
