package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.symbols.SymbolException;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolType;
import dk.nikolajbrinch.assembler.compiler.values.BinaryMath;
import dk.nikolajbrinch.assembler.compiler.values.IllegalMathOperationException;
import dk.nikolajbrinch.assembler.compiler.values.IntegerMath;
import dk.nikolajbrinch.assembler.compiler.values.Logic;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.StringValue;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import dk.nikolajbrinch.assembler.parser.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.parser.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.parser.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.parser.expressions.NumberExpression;
import dk.nikolajbrinch.assembler.parser.expressions.StringExpression;
import dk.nikolajbrinch.assembler.parser.expressions.MacroCallExpression;
import dk.nikolajbrinch.assembler.parser.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import java.util.Optional;

public class ExpressionEvaluator implements ExpressionVisitor<Value<?>> {

  private SymbolTable symbolTable;
  private Address currentAddress;

  public Value<?> visitBinaryExpression(BinaryExpression expression) {
    Value<?> left = evaluate(expression.left());
    Value<?> right = evaluate(expression.right());

    try {
      return switch (expression.operator().type()) {
        case PLUS -> IntegerMath.add(left, right);
        case MINUS -> IntegerMath.subtract(left, right);
        case STAR -> IntegerMath.multiply(left, right);
        case SLASH -> IntegerMath.divide(left, right);
        case EQUAL_EQUAL -> Logic.compare(left, right);
        case AND -> BinaryMath.and(left, right);
        case AND_AND -> Logic.and(left, right);
        case PIPE -> BinaryMath.or(left, right);
        case PIPE_PIPE -> Logic.or(left, right);
        case CARET -> BinaryMath.xor(left, right);
        case GREATER_GREATER -> BinaryMath.shiftRight(left, right);
        case LESS_LESS -> BinaryMath.shiftLeft(left, right);
        case GREATER_GREATER_GREATER -> Logic.shiftRight(left, right);
        default -> throw new IllegalStateException("Unknown binary expression");
      };
    } catch (IllegalMathOperationException e) {
      throw new EvaluationException(expression, e.getMessage(), e);
    }
  }

  @Override
  public Value<?> visitUnaryExpression(UnaryExpression expression) {
    Value<?> value = evaluate(expression.expression());

    try {
      return switch (expression.operator().type()) {
        case MINUS -> IntegerMath.negate(value);
        case PLUS -> value;
        case BANG -> Logic.not(value);
        case TILDE -> BinaryMath.not(value);
        default -> throw new IllegalStateException("Unknown unary expression");
      };
    } catch (IllegalMathOperationException e) {
      throw new EvaluationException(expression, e.getMessage(), e);
    }
  }

  @Override
  public Value<?> visitGroupingExpression(GroupingExpression expression) {
    return expression.expression().accept(this);
  }

  @Override
  public Value<?> visitNumberExpression(NumberExpression expression) {
    return expression.numberValue();
  }

  @Override
  public Value<?> visitStringExpression(StringExpression expression) {
    return expression.stringValue();
  }

  @Override
  public Value<?> visitIdentifierExpression(IdentifierExpression expression) {
    Optional<?> optional = null;

    try {
      optional = symbolTable.get(expression.token().text());
    } catch (SymbolException e) {
      throw new EvaluationException(expression, e.getMessage(), e);
    }

    if (optional.isPresent()) {
      Object symbol = optional.get();

      if (symbol instanceof Value<?> value) {
        return value;
      }
    } else {
      SymbolType symbolType = symbolTable.getSymbolType(expression.token().text());

      throw new EvaluationException(
          expression,
          "Undefined value for " + symbolType + " identifier: " + expression.token().text());
    }

    return null;
  }

  @Override
  public Value<?> visitMacroCallExpression(MacroCallExpression macroCallExpression) {
    return null;
  }

  @Override
  public Value<?> visitAddressExpression(AddressExpression expression) {
    if (expression.token().type() == AssemblerTokenType.DOLLAR) {
      return currentAddress.logicalAddress();
    }

    if (expression.token().type() == AssemblerTokenType.DOLLAR_DOLLAR) {
      return currentAddress.physicalAddress();
    }

    throw new IllegalStateException("Unknown address expression");
  }

  private <T extends Value<T>> T evaluate(Expression expression) {
    return (T) expression.accept(this);
  }

  public <T extends Value<T>> T evaluate(Expression expression, SymbolTable symbolTable) {
    this.symbolTable = symbolTable;

    return (T) expression.accept(this);
  }

  public <T extends Value<T>> T evaluate(
      Expression expression, SymbolTable symbolTable, Address currentAddress) {
    this.symbolTable = symbolTable;
    this.currentAddress = currentAddress;

    return (T) expression.accept(this);
  }

}
