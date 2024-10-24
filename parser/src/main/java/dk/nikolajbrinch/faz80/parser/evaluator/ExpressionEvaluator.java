package dk.nikolajbrinch.faz80.parser.evaluator;

import dk.nikolajbrinch.faz80.parser.base.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.base.values.NumberValue.Size;
import dk.nikolajbrinch.faz80.parser.base.values.Value;
import dk.nikolajbrinch.faz80.parser.expressions.AddressExpression;
import dk.nikolajbrinch.faz80.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.parser.expressions.ExpressionProcessor;
import dk.nikolajbrinch.faz80.parser.expressions.GroupingExpression;
import dk.nikolajbrinch.faz80.parser.expressions.IdentifierExpression;
import dk.nikolajbrinch.faz80.parser.expressions.MacroCallExpression;
import dk.nikolajbrinch.faz80.parser.expressions.NumberExpression;
import dk.nikolajbrinch.faz80.parser.expressions.StringExpression;
import dk.nikolajbrinch.faz80.parser.expressions.UnaryExpression;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolException;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolType;
import dk.nikolajbrinch.faz80.parser.values.BinaryMath;
import dk.nikolajbrinch.faz80.parser.values.IllegalMathOperationException;
import dk.nikolajbrinch.faz80.parser.values.IntegerMath;
import dk.nikolajbrinch.faz80.parser.values.Logic;
import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;
import java.util.Optional;

public class ExpressionEvaluator implements ExpressionProcessor<Evaluated> {

  private Context context;

  public Evaluated processBinaryExpression(BinaryExpression expression) {
    Evaluated left = evaluate(expression.left());
    Evaluated right = evaluate(expression.right());

    try {
      Evaluated evaluated =
          Evaluated.of(
              () ->
                  switch (expression.operator().type()) {
                    case PLUS -> IntegerMath.add(left.val(), right.val());
                    case MINUS -> IntegerMath.subtract(left.val(), right.val());
                    case STAR -> IntegerMath.multiply(left.val(), right.val());
                    case SLASH -> IntegerMath.divide(left.val(), right.val());
                    case EQUAL_EQUAL -> Logic.compare(left.val(), right.val());
                    case AND -> BinaryMath.and(left.val(), right.val());
                    case AND_AND -> Logic.and(left.val(), right.val());
                    case PIPE -> BinaryMath.or(left.val(), right.val());
                    case PIPE_PIPE -> Logic.or(left.val(), right.val());
                    case CARET -> BinaryMath.xor(left.val(), right.val());
                    case GREATER_GREATER -> BinaryMath.shiftRight(left.val(), right.val());
                    case LESS_LESS -> BinaryMath.shiftLeft(left.val(), right.val());
                    case GREATER_GREATER_GREATER -> Logic.shiftRight(left.val(), right.val());
                    default -> throw new IllegalStateException("Unknown binary expression");
                  },
              Size.values()[Math.max(left.size().ordinal(), right.size().ordinal())]);

      return evaluated.isLazy() ? evaluated : Evaluated.of(evaluated.val(), evaluated.size());
    } catch (IllegalMathOperationException e) {
      throw new EvaluationException(expression, e.getMessage(), e);
    }
  }

  @Override
  public Evaluated processUnaryExpression(UnaryExpression expression) {
    Evaluated value = evaluate(expression.expression());

    try {
      Evaluated evaluated =
          Evaluated.of(
              () ->
                  switch (expression.operator().type()) {
                    case MINUS -> IntegerMath.negate(value.val());
                    case PLUS -> value.val();
                    case BANG -> Logic.not(value.val());
                    case TILDE -> BinaryMath.not(value.val());
                    default -> throw new IllegalStateException("Unknown unary expression");
                  },
              value.size());

      return value.isLazy() ? evaluated : Evaluated.of(evaluated.val(), evaluated.size());
    } catch (IllegalMathOperationException e) {
      throw new EvaluationException(expression, e.getMessage(), e);
    }
  }

  @Override
  public Evaluated processGroupingExpression(GroupingExpression expression) {
    return process(expression.expression());
  }

  @Override
  public Evaluated processNumberExpression(NumberExpression expression) {
    return Evaluated.of(expression.numberValue(), expression.numberValue().size());
  }

  @Override
  public Evaluated processStringExpression(StringExpression expression) {
    if (expression.stringValue().canBeNumber()) {
      return Evaluated.of(
          expression.stringValue().asNumberValue(),
          expression.stringValue().asNumberValue().size());
    }

    return Evaluated.of(expression.stringValue(), Size.UNKNOWN);
  }

  @Override
  public Evaluated processIdentifierExpression(IdentifierExpression expression) {
    Evaluated evaluated = null;

    String symbolName = expression.token().text();

    if (!context.symbols().exists(symbolName)) {
      throw new EvaluationException(expression, "Undefined symbol: \"" + symbolName + "\"");
    }

    Optional<?> optional = null;

    try {
      optional = context.symbols().get(symbolName);
    } catch (SymbolException e) {
      /*
       * Ignore
       */
    }

    if (optional == null) {
      evaluated =
          Evaluated.of(
              () -> (Value<?>) context.symbols().get(expression.token().text()).get(),
              findSize(expression));
    } else {
      if (optional.isPresent()) {
        Object symbol = optional.get();

        if (symbol instanceof Value<?> value) {
          evaluated = Evaluated.of(value, findSize(expression));
        }
      } else {
        SymbolType symbolType = context.symbols().getSymbolType(expression.token().text());

        throw new EvaluationException(
            expression,
            "Undefined value for " + symbolType + " identifier: " + expression.token().text());
      }
    }

    return evaluated;
  }

  @Override
  public Evaluated processMacroCallExpression(MacroCallExpression expression) {
    return null;
  }

  @Override
  public Evaluated processAddressExpression(AddressExpression expression) {
    if (expression.token().type() == AssemblerTokenType.DOLLAR) {
      return Evaluated.of(context.currentAddress().logicalAddress(), Size.WORD);
    }

    if (expression.token().type() == AssemblerTokenType.DOLLAR_DOLLAR) {
      return Evaluated.of(context.currentAddress().physicalAddress(), Size.WORD);
    }

    throw new IllegalStateException("Unknown address expression");
  }

  private Evaluated evaluate(Expression expression) {
    return process(expression);
  }

  public Evaluated evaluate(Expression expression, Context context) {
    this.context = context;

    return process(expression);
  }

  private Size findSize(IdentifierExpression expression) {
    SymbolType symbolType = context.symbols().getSymbolType(expression.token().text());

    if (symbolType != null && symbolType == SymbolType.LABEL) {
      return Size.WORD;
    }

    Optional<?> optional = null;

    try {
      optional = context.symbols().get(expression.token().text());
    } catch (SymbolException e) {
      /*
       * Ignore
       */
    }

    if (optional != null && optional.isPresent()) {
      Object symbol = optional.get();

      if (symbol instanceof NumberValue value) {
        return value.size();
      }
    }

    return Size.UNKNOWN;
  }
}
