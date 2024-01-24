package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.operands.Context;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolException;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolType;
import dk.nikolajbrinch.assembler.compiler.values.BinaryMath;
import dk.nikolajbrinch.assembler.compiler.values.IllegalMathOperationException;
import dk.nikolajbrinch.assembler.compiler.values.IntegerMath;
import dk.nikolajbrinch.assembler.compiler.values.Logic;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import dk.nikolajbrinch.assembler.parser.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.parser.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.parser.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.parser.expressions.MacroCallExpression;
import dk.nikolajbrinch.assembler.parser.expressions.NumberExpression;
import dk.nikolajbrinch.assembler.parser.expressions.StringExpression;
import dk.nikolajbrinch.assembler.parser.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import java.util.Optional;

public class ExpressionEvaluator implements ExpressionVisitor<Evaluated> {

  private Context context;

  public Evaluated visitBinaryExpression(BinaryExpression expression) {
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
  public Evaluated visitUnaryExpression(UnaryExpression expression) {
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
  public Evaluated visitGroupingExpression(GroupingExpression expression) {
    return expression.expression().accept(this);
  }

  @Override
  public Evaluated visitNumberExpression(NumberExpression expression) {
    return Evaluated.of(expression.numberValue(), expression.numberValue().size());
  }

  @Override
  public Evaluated visitStringExpression(StringExpression expression) {
    if (expression.stringValue().canBeNUmber()) {
      return Evaluated.of(
          expression.stringValue().asNumberValue(),
          expression.stringValue().asNumberValue().size());
    }

    return Evaluated.of(expression.stringValue(), Size.UNKNOWN);
  }

  @Override
  public Evaluated visitIdentifierExpression(IdentifierExpression expression) {
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
  public Evaluated visitMacroCallExpression(MacroCallExpression macroCallExpression) {
    return null;
  }

  @Override
  public Evaluated visitAddressExpression(AddressExpression expression) {
    if (expression.token().type() == AssemblerTokenType.DOLLAR) {
      return Evaluated.of(context.currentAddress().logicalAddress(), Size.WORD);
    }

    if (expression.token().type() == AssemblerTokenType.DOLLAR_DOLLAR) {
      return Evaluated.of(context.currentAddress().physicalAddress(), Size.WORD);
    }

    throw new IllegalStateException("Unknown address expression");
  }

  private Evaluated evaluate(Expression expression) {
    return expression.accept(this);
  }

  public Evaluated evaluate(Expression expression, Context context) {
    this.context = context;

    return expression.accept(this);
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
