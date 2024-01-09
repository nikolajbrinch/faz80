package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.ast.expressions.AssignExpression;
import dk.nikolajbrinch.assembler.ast.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.ast.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.ast.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.ast.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.ast.expressions.LiteralExpression;
import dk.nikolajbrinch.assembler.ast.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolType;
import dk.nikolajbrinch.assembler.compiler.symbols.ValueSymbol;
import dk.nikolajbrinch.assembler.compiler.values.BinaryMath;
import dk.nikolajbrinch.assembler.compiler.values.IntegerMath;
import dk.nikolajbrinch.assembler.compiler.values.Logic;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.StringValue;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import dk.nikolajbrinch.assembler.scanner.AssemblerTokenType;

public class ExpressionEvaluator implements ExpressionVisitor<Value<?>> {

  private SymbolTable symbolTable;
  private Address currentAddress;

  public Value<?> visitBinaryExpression(BinaryExpression expression) {
    Value<?> left = evaluate(expression.left());
    Value<?> right = evaluate(expression.right());

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
  }

  @Override
  public Value<?> visitUnaryExpression(UnaryExpression expression) {
    Value<?> value = evaluate(expression.expression());

    return switch (expression.operator().type()) {
      case MINUS -> IntegerMath.negate(value);
      case PLUS -> value;
      case BANG -> Logic.not(value);
      case TILDE -> BinaryMath.not(value);
      default -> throw new IllegalStateException("Unknown unary expression");
    };
  }

  @Override
  public Value<?> visitGroupingExpression(GroupingExpression expression) {
    return expression.expression().accept(this);
  }

  @Override
  public Value<?> visitLiteralExpression(LiteralExpression expression) {
    return switch (expression.token().type()) {
      case DECIMAL_NUMBER, HEX_NUMBER, OCTAL_NUMBER, BINARY_NUMBER -> NumberValue.create(
          expression.token());
      case STRING, CHAR -> StringValue.create(expression.token());
      default -> throw new IllegalStateException("Unknown literal expression");
    };
  }

  @Override
  public Value<?> visitIdentifierExpression(IdentifierExpression expression) {
    ValueSymbol symbol = symbolTable.get(expression.token().text());

    return symbol.value();
  }

  @Override
  public Value<?> visitAssignExpression(AssignExpression expression) {
    String name = expression.identifier().text();
    SymbolType type = symbolTable.getSymbolType(name);
    symbolTable.assign(name, type, new ValueSymbol(evaluate(expression.expression())));

    throw new IllegalStateException("Unknown assign expression");
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

  public <T extends Value<T>> T evaluate(Expression expression) {
    return (T) expression.accept(this);
  }

  public <T extends Value<T>> T evaluate(Expression expression, SymbolTable symbolTable) {
    this.symbolTable = symbolTable;

    return (T) expression.accept(this);
  }

  public <T extends Value<T>> T evaluate(Expression expression, SymbolTable symbolTable, Address currentAddress) {
    this.symbolTable = symbolTable;
    this.currentAddress = currentAddress;

    return (T) expression.accept(this);
  }
}
