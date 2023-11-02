package dk.nikolajbrinch.assembler.util;

import dk.nikolajbrinch.assembler.parser.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.parser.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.parser.expressions.AssignExpression;
import dk.nikolajbrinch.assembler.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
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
import dk.nikolajbrinch.assembler.parser.statements.LabelStatement;
import dk.nikolajbrinch.assembler.parser.statements.LocalStatement;
import dk.nikolajbrinch.assembler.parser.statements.LongStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroStatement;
import dk.nikolajbrinch.assembler.parser.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.parser.statements.OriginStatement;
import dk.nikolajbrinch.assembler.parser.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.parser.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.parser.statements.StatementVisitor;
import dk.nikolajbrinch.assembler.parser.statements.VariableStatement;
import dk.nikolajbrinch.assembler.parser.statements.WordStatement;
import dk.nikolajbrinch.assembler.scanner.Token;
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
    return parenthesize(statement.mnemonic().text(), statement.left(), statement.right());
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
        "block: " + statement.statements().stream().map(s -> s.accept(this)).collect(Collectors.joining("\n")));
  }

  @Override
  public String visitLocalStatement(LocalStatement statement) {
    return parenthesize("local: " + statement.block().accept(this));
  }

  @Override
  public String visitMacroStatement(MacroStatement statement) {
    return parenthesize("macro: [" + statement.name() + "] (" +
        statement.parameters().stream()
            .map(parameter -> parameter.name().text() + defaultValue(parameter))
            .collect(Collectors.joining(",")) + ") " +
        statement.block().accept(this));
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
    return "if: " + parenthesize("", statement.condition()) + statement.thenBranch().accept(this)
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
    return parenthesize("call: " + statement.name(), statement.arguments().toArray(new Expression[0]));
  }

  @Override
  public String visitEndStatement(EndStatement endStatement) {
    return parenthesize("end");
  }

  @Override
  public String visitAssignExpression(AssignExpression expression) {
    return parenthesize("assign: " + expression.identifier().text(), expression.expression());
  }

  @Override
  public String visitRegisterExpression(RegisterExpression expression) {
    return "register: " + expression.register();
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


}
