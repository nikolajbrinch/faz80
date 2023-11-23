package dk.nikolajbrinch.assembler.util;

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
import dk.nikolajbrinch.assembler.ast.statements.IncludeStatement;
import dk.nikolajbrinch.assembler.ast.statements.InsertStatement;
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
import dk.nikolajbrinch.assembler.parser.Parameter;
import java.util.List;
import java.util.stream.Collectors;

public class AstPrinter implements ExpressionVisitor<String>, StatementVisitor<String> {

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
  public String visitLiteralExpression(LiteralExpression expression) {
    return expression.token().text();
  }

  @Override
  public String visitIdentifierExpression(IdentifierExpression expression) {
    return parenthesize("identifier: " + expression.token());
  }

  @Override
  public String visitAddressExpression(AddressExpression expression) {
    return parenthesize(expression.token().text());
  }

  @Override
  public String visitExpressionStatement(ExpressionStatement statement) {
    return parenthesize("expression", statement.expression());
  }

  @Override
  public String visitInstructionStatement(InstructionStatement statement) {
    return parenthesize(
        "instruction: " + statement.mnemonic().text(), statement.operand1(), statement.operand2());
  }

  @Override
  public String visitConstantStatement(ConstantStatement statement) {
    return parenthesize("constant: " + statement.identifier().text(), statement.value());
  }

  @Override
  public String visitVariableStatement(VariableStatement statement) {
    return parenthesize("variable: " + statement.identifier().text(), statement.intializer());
  }

  @Override
  public String visitByteStatement(ByteStatement statement) {
    return parenthesize("byte: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String visitLongStatement(LongStatement statement) {
    return parenthesize("long: ", statement.values().toArray(new Expression[0]));
  }

  @Override
  public String visitWordStatement(WordStatement statement) {
    return parenthesize("word: ", statement.values().toArray(new Expression[0]));
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
    return parenthesize(
        "block: "
            + statement.statements().stream()
                .map(s -> s.accept(this))
                .collect(Collectors.joining("\n")));
  }

  @Override
  public String visitLocalStatement(LocalStatement statement) {
    return parenthesize("local: " + statement.block().accept(this));
  }

  @Override
  public String visitMacroStatement(MacroStatement statement) {
    return parenthesize(
        "macro: ["
            + statement.name()
            + "] ("
            + statement.parameters().stream()
                .map(parameter -> parameter.name().text() + defaultValue(parameter))
                .collect(Collectors.joining(","))
            + ") "
            + statement.block().accept(this));
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
    return parenthesize("global: " + statement.identifier());
  }

  @Override
  public String visitLabelStatement(LabelStatement statement) {
    return parenthesize("Labels: " + statement.identifier().text());
  }

  @Override
  public String visitMacroCallStatement(MacroCallStatement statement) {
    return parenthesize("call: " + statement.name(), statement.arguments());
  }

  @Override
  public String visitEmptyStatement(EmptyStatement statement) {
    return parenthesize("empty");
  }

  @Override
  public String visitIncludeStatement(IncludeStatement statement) {
    return parenthesize("include: " + statement.string());
  }

  @Override
  public String visitInsertStatement(InsertStatement statement) {
    return parenthesize("insert: " + statement.string());
  }

  @Override
  public String visitAssignExpression(AssignExpression expression) {
    return parenthesize("assign: " + expression.identifier().text(), expression.expression());
  }

  @Override
  public String visitRegisterExpression(RegisterExpression expression) {
    return "register: " + expression.register();
  }

  @Override
  public String visitConditionExpression(ConditionExpression expression) {
    return "condition: " + expression.condition();
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
