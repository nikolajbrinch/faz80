package dk.nikolajbrinch.assembler.parser.compiler;

import dk.nikolajbrinch.assembler.parser.Environment;
import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.assembler.parser.compiler.NumberValue.Size;
import dk.nikolajbrinch.assembler.parser.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.parser.expressions.AssignExpression;
import dk.nikolajbrinch.assembler.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.parser.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.parser.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.parser.expressions.LiteralExpression;
import dk.nikolajbrinch.assembler.parser.expressions.RegisterExpression;
import dk.nikolajbrinch.assembler.parser.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.parser.statements.AlignStatement;
import dk.nikolajbrinch.assembler.parser.statements.AssertStatement;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.assembler.parser.statements.ByteStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.parser.statements.EndStatement;
import dk.nikolajbrinch.assembler.parser.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.parser.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.parser.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.parser.statements.LabelStatement;
import dk.nikolajbrinch.assembler.parser.statements.LocalStatement;
import dk.nikolajbrinch.assembler.parser.statements.LongStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroStatement;
import dk.nikolajbrinch.assembler.parser.statements.OriginStatement;
import dk.nikolajbrinch.assembler.parser.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.parser.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.parser.statements.StatementVisitor;
import dk.nikolajbrinch.assembler.parser.statements.VariableStatement;
import dk.nikolajbrinch.assembler.parser.statements.WordStatement;
import dk.nikolajbrinch.assembler.scanner.TokenType;
import java.util.List;
import java.util.function.Supplier;

public class Compiler implements StatementVisitor<Void>, ExpressionVisitor<Object> {

  private final Environment globals = new Environment();

  private Environment environment = globals;

  private NumberValue currentAddress = NumberValue.create(0);

  public void compile(List<Statement> statements) {
    try {
      for (Statement statement : statements) {
        execute(statement);
      }
    } catch (EndException e) {
      /*
       * End of code reached
       */
    }

    System.out.println(globals);
  }

  @Override
  public Void visitExpressionStatement(ExpressionStatement statement) {
    evaluate(statement.expression());

    return null;
  }

  @Override
  public Void visitInstructionStatement(InstructionStatement statement) {
    Object left = statement.left() == null ? null : validateOperand(evaluate(statement.left()));
    Object right = statement.right() == null ? null : validateOperand(evaluate(statement.right()));

    System.out.println(statement.mnemonic()
        + (left == null ? "" : " " + left)
        + (right == null ? "" : ", " + right)
    );

    return null;
  }

  private Object validateOperand(Object operand) {
    if (operand instanceof Register) {
      return operand;
    }

    if (operand instanceof NumberValue n && (n.size() == Size.BYTE || n.size() == Size.WORD)) {
      return operand;
    }

    if (operand instanceof StringValue s) {
      return s.asNumberValue();
    }

    if (operand instanceof Boolean b) {
      return b ? NumberValue.create(1) : NumberValue.create(0);
    }

    throw new IllegalStateException("Invalid operand");
  }

  @Override
  public Void visitConstantStatement(ConstantStatement statement) {
    environment.define(statement.identifier().text(), evaluate(statement.value()));

    return null;
  }

  @Override
  public Void visitVariableStatement(VariableStatement statement) {
    environment.define(statement.identifier().text(), evaluate(statement.intializer()));

    return null;
  }

  @Override
  public Void visitByteStatement(ByteStatement statement) {
    return null;
  }

  @Override
  public Void visitLongStatement(LongStatement statement) {
    return null;
  }

  @Override
  public Void visitWordStatement(WordStatement statement) {
    return null;
  }

  @Override
  public Void visitOriginStatement(OriginStatement statement) {
    Object location = evaluate(statement.location());

    if (location instanceof NumberValue value) {
      currentAddress = value.asWord();
    } else {
      throw new IllegalStateException("Origin is not a number");
    }

    return null;
  }

  @Override
  public Void visitAlignStatement(AlignStatement statement) {
    return null;
  }

  @Override
  public Void visitBlockStatement(BlockStatement statement) {
    statement.statements().forEach(this::execute);

    return null;
  }

  @Override
  public Void visitLocalStatement(LocalStatement statement) {
    withEnvironment(new Environment(environment), () -> statement.block().accept(this));

    return null;
  }

  @Override
  public Void visitMacroStatement(MacroStatement statement) {
    environment.define(statement.name().text(), statement);

    return null;
  }

  @Override
  public Void visitPhaseStatement(PhaseStatement statement) {
    statement.block().accept(this);

    return null;
  }

  @Override
  public Void visitRepeatStatement(RepeatStatement statement) {
    Object count = evaluate(statement.count());

    if (count instanceof NumberValue value) {
      for (int i = 0; i < value.value(); i++) {
        statement.blockStatement().accept(this);
      }
    } else {
      throw new IllegalStateException("Repeat count is not a number");
    }

    return null;
  }

  @Override
  public Void visitConditionalStatement(ConditionalStatement statement) {
    Object result = evaluate(statement.condition());

    if (result instanceof Boolean value) {
      if (value) {
        statement.thenBranch().accept(this);
      } else {
        if (statement.elseBranch() != null) {
          statement.elseBranch().accept(this);
        }
      }
    } else {
      throw new IllegalStateException("Condition is not boolean");
    }

    return null;
  }

  @Override
  public Void visitAssertStatement(AssertStatement statement) {
    Object result = evaluate(statement.expression());

    if (result instanceof Boolean value) {
      if (!value) {
        throw new AssertionError("Assertion failed");
      }
    } else {
      throw new IllegalStateException("Condition is not boolean");
    }

    return null;
  }

  @Override
  public Void visitGlobalStatement(GlobalStatement statement) {
    globals.define(statement.identifier().text(), null);

    return null;
  }

  @Override
  public Void visitLabelStatement(LabelStatement statement) {
    globals.define(statement.identifier().text(), currentAddress);

    return null;
  }

  @Override
  public Void visitMacroCallStatement(MacroCallStatement statement) {
    MacroStatement macro = (MacroStatement) environment.get(statement.name().text());

    Environment macroEnvironment = new Environment(environment);

    for (int i = 0; i < Math.max(statement.arguments().size(), macro.parameters().size()); i++) {
      Expression expression = i < statement.arguments().size() ? statement.arguments().get(i) : null;
      Parameter parameter = i < macro.parameters().size() ? macro.parameters().get(i) : null;

      if (parameter != null) {
        String name = parameter.name().text();

        Object value = null;

        if (expression != null) {
          value = evaluate(expression);
        } else if (parameter.defaultValue() != null) {
          value = evaluate(parameter.defaultValue());
        }

        macroEnvironment.define(name, value);
      }
    }

    withEnvironment(macroEnvironment, () -> macro.block().accept(this));

    return null;
  }

  @Override
  public Void visitEndStatement(EndStatement statement) {
    throw new EndException(statement.end().text());
  }

  private void execute(Statement statement) {
    statement.accept(this);
  }

  @Override
  public Object visitBinaryExpression(BinaryExpression expression) {
    Object left = evaluate(expression.left());
    Object right = evaluate(expression.right());

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
  public Object visitUnaryExpression(UnaryExpression expression) {
    Object value = evaluate(expression.expression());

    return switch (expression.operator().type()) {
      case MINUS -> IntegerMath.negate(value);
      case PLUS -> value;
      case BANG -> Logic.not(value);
      case TILDE -> BinaryMath.not(value);
      default -> throw new IllegalStateException("Unknown unary expression");
    };
  }

  @Override
  public Object visitGroupingExpression(GroupingExpression expression) {
    return expression.expression().accept(this);
  }

  @Override
  public Object visitLiteralExpression(LiteralExpression expression) {
    return switch (expression.token().type()) {
      case DECIMAL_NUMBER, HEX_NUMBER, OCTAL_NUMBER, BINARY_NUMBER -> NumberValue.create(expression.token());
      case STRING, CHAR -> StringValue.create(expression.token());
      default -> throw new IllegalStateException("Unknown literal expression");
    };
  }

  @Override
  public Object visitIdentifierExpression(IdentifierExpression expression) {
    return environment.get(expression.token().text());
  }

  @Override
  public Void visitAssignExpression(AssignExpression expression) {
    environment.assign(expression.identifier().text(), evaluate(expression.expression()));

    throw new IllegalStateException("Unknown assign expression");
  }

  @Override
  public Object visitRegisterExpression(RegisterExpression expression) {
    return expression.register();
  }

  @Override
  public Object visitAddressExpression(AddressExpression expression) {
    if (expression.token().type() == TokenType.DOLLAR) {
      return currentAddress;
    }

    if (expression.token().type() == TokenType.DOLLAR_DOLLAR) {
      return currentAddress;
    }

    throw new IllegalStateException("Unknown address expression");
  }

  private Object evaluate(Expression expression) {
    return expression.accept(this);
  }

  private void withEnvironment(Environment environment, Supplier<Void> supplier) {
    Environment previous = this.environment;

    try {
      this.environment = environment;
      supplier.get();
    } finally {
      this.environment = previous;
    }
  }
}
