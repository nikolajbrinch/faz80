package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.statements.IncludeStatement;
import dk.nikolajbrinch.assembler.ast.statements.InsertStatement;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Environment;
import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.ast.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.ast.expressions.AssignExpression;
import dk.nikolajbrinch.assembler.ast.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.ast.expressions.ConditionExpression;
import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.ast.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.ast.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.ast.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.ast.expressions.LiteralExpression;
import dk.nikolajbrinch.assembler.ast.expressions.RegisterExpression;
import dk.nikolajbrinch.assembler.ast.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.ast.statements.AlignStatement;
import dk.nikolajbrinch.assembler.ast.statements.AssertStatement;
import dk.nikolajbrinch.assembler.ast.statements.BlockStatement;
import dk.nikolajbrinch.assembler.ast.statements.ByteStatement;
import dk.nikolajbrinch.assembler.ast.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.ast.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.ast.statements.EmptyStatement;
import dk.nikolajbrinch.assembler.ast.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.ast.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.ast.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.ast.statements.LabelStatement;
import dk.nikolajbrinch.assembler.ast.statements.LocalStatement;
import dk.nikolajbrinch.assembler.ast.statements.LongStatement;
import dk.nikolajbrinch.assembler.ast.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.ast.statements.MacroStatement;
import dk.nikolajbrinch.assembler.ast.statements.OriginStatement;
import dk.nikolajbrinch.assembler.ast.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.ast.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.ast.statements.StatementVisitor;
import dk.nikolajbrinch.assembler.ast.statements.VariableStatement;
import dk.nikolajbrinch.assembler.ast.statements.WordStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MacroResolver implements StatementVisitor<Statement>, ExpressionVisitor<Expression> {

  private final ExpressionEvaluator expressionEvaluator;
  private final Environment globals = new Environment();

  private Environment environment = globals;

  public MacroResolver(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  public List<Statement> resolve(List<Statement> statements) {
    List<Statement> resolvedStatements = new ArrayList<>();

    for (Statement statement : statements) {
      resolvedStatements.add(statement.accept(this));
    }

    return resolvedStatements;
  }

  @Override
  public Expression visitBinaryExpression(BinaryExpression expression) {
    Expression left = evaluate(expression.left());
    Expression right = evaluate(expression.right());

    return new BinaryExpression(left, expression.operator(), right);
  }

  @Override
  public Expression visitUnaryExpression(UnaryExpression expression) {
    return new UnaryExpression(expression.operator(), evaluate(expression.expression()));
  }

  @Override
  public Expression visitGroupingExpression(GroupingExpression expression) {
    return new GroupingExpression(evaluate(expression.expression()));
  }

  @Override
  public Expression visitLiteralExpression(LiteralExpression expression) {
    return expression;
  }

  @Override
  public Expression visitIdentifierExpression(IdentifierExpression expression) {
    Object value = environment.get(expression.token().text());

    Expression result = expression;

    if (value instanceof ExpressionStatement expressionStatement) {
      result = expressionStatement.expression();
    } else if (value instanceof LabelStatement labelStatement) {
      result = new IdentifierExpression(labelStatement.identifier());
    }

    return result;
  }

  @Override
  public Expression visitAddressExpression(AddressExpression expression) {
    return expression;
  }

  @Override
  public Expression visitAssignExpression(AssignExpression expression) {
    return new AssignExpression(expression.identifier(), evaluate(expression.expression()));
  }

  @Override
  public Expression visitRegisterExpression(RegisterExpression expression) {
    return expression;
  }

  @Override
  public Expression visitConditionExpression(ConditionExpression expression) {
    return expression;
  }

  @Override
  public Statement visitExpressionStatement(ExpressionStatement statement) {
    return new ExpressionStatement(evaluate(statement.expression()));
  }

  @Override
  public Statement visitInstructionStatement(InstructionStatement statement) {
    return new InstructionStatement(
        statement.mnemonic(), evaluate(statement.operand1()), evaluate(statement.operand2()));
  }

  @Override
  public Statement visitConstantStatement(ConstantStatement statement) {
    environment.define(statement.identifier().text(), statement.value());

    return statement;
  }

  @Override
  public Statement visitVariableStatement(VariableStatement statement) {
    environment.define(statement.identifier().text(), statement.intializer());

    return statement;
  }

  @Override
  public Statement visitByteStatement(ByteStatement statement) {
    return statement;
  }

  @Override
  public Statement visitLongStatement(LongStatement statement) {
    return statement;
  }

  @Override
  public Statement visitWordStatement(WordStatement statement) {
    return statement;
  }

  @Override
  public Statement visitOriginStatement(OriginStatement statement) {
    return statement;
  }

  @Override
  public Statement visitAlignStatement(AlignStatement statement) {
    return statement;
  }

  @Override
  public Statement visitBlockStatement(BlockStatement statement) {
    return new BlockStatement(statement.statements().stream().map(this::processStatement).toList());
  }

  @Override
  public Statement visitLocalStatement(LocalStatement statement) {
    return statement;
  }

  @Override
  public Statement visitMacroStatement(MacroStatement statement) {
    globals.define(statement.name().text(), statement);

    return new EmptyStatement();
  }

  @Override
  public Statement visitPhaseStatement(PhaseStatement statement) {
    return statement;
  }

  @Override
  public Statement visitRepeatStatement(RepeatStatement statement) {
    Object count = statement.count();

    while (count instanceof Expression expression) {
      Object value = expressionEvaluator.evaluate(expression, environment, NumberValue.create(0));

      if (count == value) {
        throw new IllegalStateException("Expression refers itself!");
      }

      count = value;
    }

    List<Statement> statements = new ArrayList<>();

    if (count instanceof NumberValue value) {
      for (int i = 0; i < value.value(); i++) {
        statements.add(statement.blockStatement().accept(this));
      }
    } else {
      throw new IllegalStateException("Repeat count is not a number");
    }

    return new BlockStatement(statements);
  }

  @Override
  public Statement visitConditionalStatement(ConditionalStatement statement) {
    return statement;
  }

  @Override
  public Statement visitAssertStatement(AssertStatement statement) {
    return statement;
  }

  @Override
  public Statement visitGlobalStatement(GlobalStatement statement) {
    return statement;
  }

  @Override
  public Statement visitLabelStatement(LabelStatement statement) {
    environment.define(statement.identifier().text(), statement);

    return statement;
  }

  @Override
  public Statement visitMacroCallStatement(MacroCallStatement statement) {
    return processMacro(statement);
  }

  @Override
  public Statement visitEmptyStatement(EmptyStatement statement) {
    return statement;
  }

  @Override
  public Statement visitIncludeStatement(IncludeStatement statement) {
    return statement;
  }

  @Override
  public Statement visitInsertStatement(InsertStatement statement) {
    return statement;
  }

  private Statement processMacro(MacroCallStatement statement) {
    MacroStatement macro = (MacroStatement) environment.get(statement.name().text());

    Environment macroEnvironment = new Environment(environment);

    for (int i = 0; i < Math.max(statement.arguments().size(), macro.parameters().size()); i++) {
      Statement argument = i < statement.arguments().size() ? statement.arguments().get(i) : null;
      Parameter parameter = i < macro.parameters().size() ? macro.parameters().get(i) : null;

      if (parameter != null) {
        String name = parameter.name().text();

        Expression defaultValue = parameter.defaultValue();

        if (defaultValue != null) {
          macroEnvironment.define(name, new ExpressionStatement(defaultValue));
        }

        if (!(defaultValue != null && argument == null)) {
          macroEnvironment.define(name, argument);
        }
      }
    }

    return withEnvironment(macroEnvironment, () -> macro.block().accept(this));
  }

  private Statement processStatement(Statement statement) {
    return statement.accept(this);
  }

  private Expression evaluate(Expression expression) {
    return expression == null ? null : expression.accept(this);
  }

  private Statement withEnvironment(Environment environment, Supplier<Statement> supplier) {
    Environment previous = this.environment;

    try {
      this.environment = environment;

      return supplier.get();
    } finally {
      this.environment = previous;
    }
  }
}
