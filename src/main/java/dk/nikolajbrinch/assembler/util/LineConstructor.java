package dk.nikolajbrinch.assembler.util;

import dk.nikolajbrinch.assembler.ast.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.ast.expressions.AssignExpression;
import dk.nikolajbrinch.assembler.ast.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.ast.expressions.ConditionExpression;
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
import dk.nikolajbrinch.assembler.util.LineConstructor.ConstructedLine;
import java.util.stream.Collectors;

public class LineConstructor
    implements ExpressionVisitor<String>, StatementVisitor<ConstructedLine> {

  public record ConstructedLine(int number, String content) {}

  public ConstructedLine constructLine(Statement statement) {
    return statement.accept(this);
  }

  @Override
  public String visitBinaryExpression(BinaryExpression expression) {
    return String.format(
        "%s %s %s",
        expression.left().accept(this),
        expression.operator().text(),
        expression.right().accept(this));
  }

  @Override
  public String visitUnaryExpression(UnaryExpression expression) {
    return String.format(
        "%s%s", expression.operator().text(), expression.expression().accept(this));
  }

  @Override
  public String visitGroupingExpression(GroupingExpression expression) {
    return String.format("(%s)", expression.expression().accept(this));
  }

  @Override
  public String visitLiteralExpression(LiteralExpression expression) {
    return expression.token().text();
  }

  @Override
  public String visitIdentifierExpression(IdentifierExpression expression) {
    return expression.token().text();
  }

  @Override
  public String visitAddressExpression(AddressExpression expression) {
    return expression.token().text();
  }

  @Override
  public String visitAssignExpression(AssignExpression expression) {
    return String.format(
        "%s = %s", expression.identifier().text(), expression.expression().accept(this));
  }

  @Override
  public String visitRegisterExpression(RegisterExpression expression) {
    return expression.register().name();
  }

  @Override
  public String visitConditionExpression(ConditionExpression expression) {
    return expression.condition().name();
  }

  @Override
  public ConstructedLine visitExpressionStatement(ExpressionStatement statement) {
    return new ConstructedLine(-1, statement.expression().accept(this));
  }

  @Override
  public ConstructedLine visitInstructionStatement(InstructionStatement statement) {
    return new ConstructedLine(
        statement.mnemonic().line(),
        String.format(
            "%s %s",
            statement.mnemonic().text(),
            statement.operand1().accept(this)
                + (statement.operand2() == null ? "" : ", " + statement.operand2().accept(this))));
  }

  @Override
  public ConstructedLine visitConstantStatement(ConstantStatement statement) {
    return new ConstructedLine(
        statement.identifier().line(),
        String.format("%s = %s", statement.identifier().text(), statement.value().accept(this)));
  }

  @Override
  public ConstructedLine visitVariableStatement(VariableStatement statement) {
    return new ConstructedLine(
        statement.identifier().line(),
        String.format(
            "%s = %s", statement.identifier().text(), statement.intializer().accept(this)));
  }

  @Override
  public ConstructedLine visitByteStatement(ByteStatement statement) {
    return new ConstructedLine(
        -1,
        String.format(
            ".db %s",
            statement.values().stream()
                .map(e -> e.accept(this))
                .collect(Collectors.joining(", "))));
  }

  @Override
  public ConstructedLine visitLongStatement(LongStatement statement) {
    return new ConstructedLine(
        -1,
        String.format(
            ".long %s",
            statement.values().stream()
                .map(e -> e.accept(this))
                .collect(Collectors.joining(", "))));
  }

  @Override
  public ConstructedLine visitWordStatement(WordStatement statement) {
    return new ConstructedLine(
        -1,
        String.format(
            ".dw %s",
            statement.values().stream()
                .map(e -> e.accept(this))
                .collect(Collectors.joining(", "))));
  }

  @Override
  public ConstructedLine visitOriginStatement(OriginStatement statement) {
    return new ConstructedLine(
        -1,
        String.format(
            ".org %s",
            statement.location().accept(this)
                + (statement.fillByte() == null ? "" : ", " + statement.fillByte().accept(this))));
  }

  @Override
  public ConstructedLine visitAlignStatement(AlignStatement statement) {
    return new ConstructedLine(
        -1,
        String.format(
            ".align %s",
            statement.alignment().accept(this)
                + (statement.fillByte() == null ? "" : ", " + statement.fillByte().accept(this))));
  }

  @Override
  public ConstructedLine visitBlockStatement(BlockStatement statement) {
    return null;
  }

  @Override
  public ConstructedLine visitLocalStatement(LocalStatement statement) {
    return null;
  }

  @Override
  public ConstructedLine visitMacroStatement(MacroStatement statement) {
    return null;
  }

  @Override
  public ConstructedLine visitPhaseStatement(PhaseStatement statement) {
    return new ConstructedLine(-1, String.format(".phase %s", statement.expression().accept(this)));
  }

  @Override
  public ConstructedLine visitRepeatStatement(RepeatStatement statement) {
    return new ConstructedLine(-1, String.format(".repeat %s", statement.count().accept(this)));
  }

  @Override
  public ConstructedLine visitConditionalStatement(ConditionalStatement statement) {
    return new ConstructedLine(-1, String.format("#if %s", statement.condition().accept(this)));
  }

  @Override
  public ConstructedLine visitAssertStatement(AssertStatement statement) {
    return new ConstructedLine(
        -1, String.format(".assert %s", statement.expression().accept(this)));
  }

  @Override
  public ConstructedLine visitGlobalStatement(GlobalStatement statement) {
    return new ConstructedLine(
        -statement.identifier().line(), String.format(".globl %s", statement.identifier().text()));
  }

  @Override
  public ConstructedLine visitLabelStatement(LabelStatement statement) {
    return new ConstructedLine(
        -statement.identifier().line(), String.format("%s", statement.identifier().text()));
  }

  @Override
  public ConstructedLine visitMacroCallStatement(MacroCallStatement statement) {
    return new ConstructedLine(
        statement.name().line(),
        String.format(
            "%s %s",
            statement.name().text(),
            statement.arguments().stream()
                .map(s -> s.accept(this))
                .map(ConstructedLine::content)
                .collect(Collectors.joining(", "))));
  }

  @Override
  public ConstructedLine visitEmptyStatement(EmptyStatement statement) {
    return null;
  }

  @Override
  public ConstructedLine visitIncludeStatement(IncludeStatement statement) {
    return new ConstructedLine(
        statement.string().line(), String.format(".include %s", statement.string().text()));
  }

  @Override
  public ConstructedLine visitInsertStatement(InsertStatement statement) {
    return new ConstructedLine(
        statement.string().line(), String.format(".insert %s", statement.string().text()));
  }
}
