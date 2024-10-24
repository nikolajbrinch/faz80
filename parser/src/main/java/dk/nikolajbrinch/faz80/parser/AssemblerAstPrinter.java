package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.evaluator.EvaluationException;
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
import dk.nikolajbrinch.faz80.parser.operands.ConditionOperand;
import dk.nikolajbrinch.faz80.parser.operands.ExpressionOperand;
import dk.nikolajbrinch.faz80.parser.operands.GroupingOperand;
import dk.nikolajbrinch.faz80.parser.operands.Operand;
import dk.nikolajbrinch.faz80.parser.operands.OperandProcessor;
import dk.nikolajbrinch.faz80.parser.operands.RegisterOperand;
import dk.nikolajbrinch.faz80.parser.statements.AlignStatement;
import dk.nikolajbrinch.faz80.parser.statements.AssertStatement;
import dk.nikolajbrinch.faz80.parser.statements.AssignStatement;
import dk.nikolajbrinch.faz80.parser.statements.BlockStatement;
import dk.nikolajbrinch.faz80.parser.statements.CommentStatement;
import dk.nikolajbrinch.faz80.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataByteStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataLongStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataTextStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataWordStatement;
import dk.nikolajbrinch.faz80.parser.statements.EmptyStatement;
import dk.nikolajbrinch.faz80.parser.statements.EndStatement;
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
import dk.nikolajbrinch.faz80.parser.statements.SectionStatement;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import dk.nikolajbrinch.faz80.parser.statements.StatementProcessor;
import java.util.List;
import java.util.stream.Collectors;

public class AssemblerAstPrinter
    implements ExpressionProcessor<String>, StatementProcessor<String>, OperandProcessor<String> {

  public String print(Statement statement) {
    return process(statement);
  }

  public String print(Expression expression) {
    return process(expression);
  }

  @Override
  public String processSectionStatement(SectionStatement statement) {
    return "section: " + statement.name().text();
  }

  @Override
  public String processBinaryExpression(BinaryExpression expression) {
    return parenthesize(expression.operator().text(), expression.left(), expression.right());
  }

  @Override
  public String processUnaryExpression(UnaryExpression expression) {
    return parenthesize(expression.operator().text(), expression.expression());
  }

  @Override
  public String processGroupingExpression(GroupingExpression expression) {
    return parenthesize("group", expression.expression());
  }

  @Override
  public String processNumberExpression(NumberExpression expression) {
    return expression.token().text();
  }

  @Override
  public String processStringExpression(StringExpression expression) {
    return expression.token().text();
  }

  @Override
  public String processIdentifierExpression(IdentifierExpression expression) {
    return "identifier: " + expression.token().text();
  }

  @Override
  public String processAddressExpression(AddressExpression expression) {
    return expression.token().text();
  }

  @Override
  public String processExpressionStatement(ExpressionStatement statement) {
    return parenthesize("expression", statement.expression());
  }

  @Override
  public String processInstructionStatement(InstructionStatement statement) {
    return parenthesize(
        "instruction: " + statement.mnemonic().text(),
        statement.operands().toArray(new Operand[0]));
  }

  @Override
  public String processAssignStatement(AssignStatement statement) {
    return parenthesize("assign: " + statement.identifier().text(), statement.initializer());
  }

  @Override
  public String processCommentStatement(CommentStatement statement) {
    return "comment: " + statement.comment().text();
  }

  @Override
  public String processEndStatement(EndStatement statement) {
    return statement.token().text();
  }

  @Override
  public String processDataByteStatement(DataByteStatement statement) {
    return parenthesize("byte: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String processDataWordStatement(DataWordStatement statement) {
    return parenthesize("word: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String processDataLongStatement(DataLongStatement statement) {
    return parenthesize("long: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String processDataTextStatement(DataTextStatement statement) {
    return parenthesize("text: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String processOriginStatement(OriginStatement statement) {
    return parenthesize("org: ", statement.location(), statement.fillByte());
  }

  @Override
  public String processAlignStatement(AlignStatement statement) {
    return parenthesize("align: ", statement.alignment(), statement.fillByte());
  }

  @Override
  public String processBlockStatement(BlockStatement statement) {
    return parenthesize("block: ", statement.statements());
  }

  @Override
  public String processLocalStatement(LocalStatement statement) {
    return parenthesize("local: ", statement.block().statements());
  }

  @Override
  public String processMacroStatement(MacroStatement statement) {
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
    return parameter.defaultValue() == null ? "" : "=" + process(parameter.defaultValue());
  }

  @Override
  public String processPhaseStatement(PhaseStatement statement) {
    return parenthesize("phase: " + process(statement.block()), statement.expression());
  }

  @Override
  public String processRepeatStatement(RepeatStatement statement) {
    return parenthesize("repeat: " + process(statement.block()), statement.count());
  }

  @Override
  public String processConditionalStatement(ConditionalStatement statement) {
    return "if: "
        + parenthesize("", statement.condition())
        + process(statement.thenBranch())
        + (statement.elseBranch() == null ? "" : " else: " + process(statement.elseBranch()));
  }

  @Override
  public String processAssertStatement(AssertStatement statement) {
    return parenthesize("assert", statement.expression());
  }

  @Override
  public String processGlobalStatement(GlobalStatement statement) {
    return "global: " + statement.identifier();
  }

  @Override
  public String processMacroCallStatement(MacroCallStatement statement) {
    return parenthesize("call: " + statement.name(), statement.arguments());
  }

  @Override
  public String processEmptyStatement(EmptyStatement statement) {
    return "empty";
  }

  @Override
  public String processInsertStatement(InsertStatement statement) {
    return "insert: " + statement.string();
  }

  @Override
  public String processIncludeStatement(IncludeStatement statement) {
    return "include: " + statement.string();
  }

  @Override
  public String processMacroCallExpression(MacroCallExpression expression) {
    return parenthesize("call: " + expression.name(), expression.arguments());
  }

  @Override
  public String processRegisterOperand(RegisterOperand operand) {
    return "register: " + operand.register();
  }

  @Override
  public String processConditionOperand(ConditionOperand operand) {
    return "condition: " + operand.condition();
  }

  @Override
  public String processExpressionOperand(ExpressionOperand operand) {
    return parenthesize("expression", operand.expression());
  }

  @Override
  public String processGroupingOperand(GroupingOperand operand) {
    return parenthesize("group", operand.operand());
  }

  private String parenthesize(String name, Expression... expressions) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expression expression : expressions) {
      if (expression != null) {
        builder.append(" ");
        builder.append(process(expression));
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
        builder.append(process(operand));
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
        builder.append(process(statement));
      }
    }
    builder.append(")");

    return builder.toString();
  }
}
