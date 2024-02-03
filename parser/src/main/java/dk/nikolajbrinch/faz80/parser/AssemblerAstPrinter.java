package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.evaluator.EvaluationException;
import dk.nikolajbrinch.faz80.parser.expressions.AddressExpression;
import dk.nikolajbrinch.faz80.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.parser.expressions.ExpressionVisitor;
import dk.nikolajbrinch.faz80.parser.expressions.GroupingExpression;
import dk.nikolajbrinch.faz80.parser.expressions.IdentifierExpression;
import dk.nikolajbrinch.faz80.parser.expressions.MacroCallExpression;
import dk.nikolajbrinch.faz80.parser.expressions.NumberExpression;
import dk.nikolajbrinch.faz80.parser.expressions.StringExpression;
import dk.nikolajbrinch.faz80.parser.expressions.UnaryExpression;
import dk.nikolajbrinch.faz80.parser.operands.ConditionOperand;
import dk.nikolajbrinch.faz80.parser.operands.ExpressionOperand;
import dk.nikolajbrinch.faz80.parser.operands.GroupingOperand;
import dk.nikolajbrinch.faz80.parser.operands.Operand;
import dk.nikolajbrinch.faz80.parser.operands.OperandVisitor;
import dk.nikolajbrinch.faz80.parser.operands.RegisterOperand;
import dk.nikolajbrinch.faz80.parser.statements.AlignStatement;
import dk.nikolajbrinch.faz80.parser.statements.AssertStatement;
import dk.nikolajbrinch.faz80.parser.statements.AssignStatement;
import dk.nikolajbrinch.faz80.parser.statements.BlockStatement;
import dk.nikolajbrinch.faz80.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataByteStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataLongStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataTextStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataWordStatement;
import dk.nikolajbrinch.faz80.parser.statements.EmptyStatement;
import dk.nikolajbrinch.faz80.parser.statements.ExpressionStatement;
import dk.nikolajbrinch.faz80.parser.statements.GlobalStatement;
import dk.nikolajbrinch.faz80.parser.statements.IncludeStatement;
import dk.nikolajbrinch.faz80.parser.statements.InsertStatement;
import dk.nikolajbrinch.faz80.parser.statements.InstructionStatement;
import dk.nikolajbrinch.faz80.parser.statements.LocalStatement;
import dk.nikolajbrinch.faz80.parser.statements.MacroCallStatement;
import dk.nikolajbrinch.faz80.parser.statements.MacroStatement;
import dk.nikolajbrinch.faz80.parser.statements.OriginStatement;
import dk.nikolajbrinch.faz80.parser.statements.PhaseStatement;
import dk.nikolajbrinch.faz80.parser.statements.RepeatStatement;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import dk.nikolajbrinch.faz80.parser.statements.StatementVisitor;
import java.util.List;
import java.util.stream.Collectors;

public class AssemblerAstPrinter
    implements ExpressionVisitor<String>, StatementVisitor<String>, OperandVisitor<String> {

  public String print(Statement statement) {
    return statement.accept(this);
  }

  public String print(Expression expression) {
    return expression.accept(this);
  }

  @Override
  public String visitBinaryExpression(BinaryExpression expression) {
    return parenthesize(expression.operator().text(), expression.left(), expression.right());
  }

  @Override
  public String visitUnaryExpression(UnaryExpression expression) {
    return parenthesize(expression.operator().text(), expression.expression());
  }

  @Override
  public String visitGroupingExpression(GroupingExpression expression) {
    return parenthesize("group", expression.expression());
  }

  @Override
  public String visitNumberExpression(NumberExpression expression) {
    return expression.token().text();
  }

  @Override
  public String visitStringExpression(StringExpression expression) {
    return expression.token().text();
  }

  @Override
  public String visitIdentifierExpression(IdentifierExpression expression) {
    return "identifier: " + expression.token().text();
  }

  @Override
  public String visitAddressExpression(AddressExpression expression) {
    return expression.token().text();
  }

  @Override
  public String visitExpressionStatement(ExpressionStatement statement) {
    return parenthesize("expression", statement.expression());
  }

  @Override
  public String visitInstructionStatement(InstructionStatement statement) {
    return parenthesize(
        "instruction: " + statement.mnemonic().text(),
        statement.operands().toArray(new Operand[0]));
  }

  @Override
  public String visitAssignStatement(AssignStatement statement) {
    return parenthesize("assign: " + statement.identifier().text(), statement.initializer());
  }

  @Override
  public String visitDataByteStatement(DataByteStatement statement) {
    return parenthesize("byte: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String visitDataWordStatement(DataWordStatement statement) {
    return parenthesize("word: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String visitDataLongStatement(DataLongStatement statement) {
    return parenthesize("long: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String visitDataTextStatement(DataTextStatement statement) {
    return parenthesize("text: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String visitOriginStatement(OriginStatement statement) {
    return parenthesize("org: ", statement.location(), statement.fillByte());
  }

  @Override
  public String visitAlignStatement(AlignStatement statement) {
    return parenthesize("align: ", statement.alignment(), statement.fillByte());
  }

  @Override
  public String visitBlockStatement(BlockStatement statement) {
    return parenthesize("block: ", statement.statements());
  }

  @Override
  public String visitLocalStatement(LocalStatement statement) {
    return parenthesize("local: ", statement.block().statements());
  }

  @Override
  public String visitMacroStatement(MacroStatement statement) {
    return parenthesize(
        "macro: ["
            + statement.name()
            + "] ("
            + statement.parameters().stream()
                .map(
                    parameter -> {
                      try {
                        return parameter.name().text() + defaultValue(parameter);
                      } catch (EvaluationException e) {
                        e.printStackTrace();
                        return null;
                      }
                    })
                .collect(Collectors.joining(","))
            + ") ",
        statement.block().statements());
  }

  private String defaultValue(Parameter parameter) {
    return parameter.defaultValue() == null ? "" : "=" + parameter.defaultValue().accept(this);
  }

  @Override
  public String visitPhaseStatement(PhaseStatement statement) {
    return parenthesize("phase: " + statement.block().accept(this), statement.expression());
  }

  @Override
  public String visitRepeatStatement(RepeatStatement statement) {
    return parenthesize("repeat: " + statement.blockStatement().accept(this), statement.count());
  }

  @Override
  public String visitConditionalStatement(ConditionalStatement statement) {
    return "if: "
        + parenthesize("", statement.condition())
        + statement.thenBranch().accept(this)
        + (statement.elseBranch() == null ? "" : " else: " + statement.elseBranch().accept(this));
  }

  @Override
  public String visitAssertStatement(AssertStatement statement) {
    return parenthesize("assert", statement.expression());
  }

  @Override
  public String visitGlobalStatement(GlobalStatement statement) {
    return "global: " + statement.identifier();
  }

  @Override
  public String visitMacroCallStatement(MacroCallStatement statement) {
    return parenthesize("call: " + statement.name(), statement.arguments());
  }

  @Override
  public String visitEmptyStatement(EmptyStatement statement) {
    return "empty";
  }

  @Override
  public String visitInsertStatement(InsertStatement statement) {
    return "insert: " + statement.string();
  }

  @Override
  public String visitIncludeStatement(IncludeStatement statement) {
    return "include: " + statement.string();
  }

  @Override
  public String visitMacroCallExpression(MacroCallExpression expression) {
    return parenthesize("call: " + expression.name(), expression.arguments());
  }

  @Override
  public String visitRegisterOperand(RegisterOperand operand) {
    return "register: " + operand.register();
  }

  @Override
  public String visitConditionOperand(ConditionOperand operand) {
    return "condition: " + operand.condition();
  }

  @Override
  public String visitExpressionOperand(ExpressionOperand operand) {
    return parenthesize("expression", operand.expression());
  }

  @Override
  public String visitGroupingOperand(GroupingOperand operand) {
    return parenthesize("group", operand.operand());
  }

  private String parenthesize(String name, Expression... expressions) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expression expression : expressions) {
      if (expression != null) {
        builder.append(" ");
        builder.append(expression.accept(this));
      }
    }
    builder.append(")");

    return builder.toString();
  }

  private String parenthesize(String name, Operand... operands) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Operand operand : operands) {
      if (operand != null) {
        builder.append(" ");
        builder.append(operand.accept(this));
      }
    }
    builder.append(")");

    return builder.toString();
  }

  private String parenthesize(String name, List<Statement> statements) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Statement statement : statements) {
      if (statement != null) {
        builder.append(" ");
        builder.append(statement.accept(this));
      }
    }
    builder.append(")");

    return builder.toString();
  }
}
