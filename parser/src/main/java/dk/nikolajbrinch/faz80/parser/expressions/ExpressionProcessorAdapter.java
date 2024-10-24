package dk.nikolajbrinch.faz80.parser.expressions;

public interface ExpressionProcessorAdapter<R> extends ExpressionProcessor<R> {

  default R processBinaryExpression(BinaryExpression expression) { return null; }

  default R processUnaryExpression(UnaryExpression expression) { return null; }

  default R processGroupingExpression(GroupingExpression expression) { return null; }

  default R processIdentifierExpression(IdentifierExpression expression) { return null; }

  default R processAddressExpression(AddressExpression expression) { return null; }

  default R processMacroCallExpression(MacroCallExpression expression) { return null; }

  default R processNumberExpression(NumberExpression expression) { return null; }

  default R processStringExpression(StringExpression expression) { return null; }
}
