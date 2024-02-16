package dk.nikolajbrinch.faz80.ide.ast;

import dk.nikolajbrinch.faz80.ide.symbols.SymbolTableTreeItem;
import dk.nikolajbrinch.faz80.parser.IdentifierUtil;
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
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import dk.nikolajbrinch.faz80.parser.statements.StatementVisitor;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import dk.nikolajbrinch.scanner.Line;
import javafx.scene.control.TreeItem;

public class AstTreeBuilder
    implements StatementVisitor<TreeItem<AstTreeValue>>,
        ExpressionVisitor<TreeItem<AstTreeValue>>,
        OperandVisitor<TreeItem<AstTreeValue>> {

  @Override
  public TreeItem<AstTreeValue> visitBinaryExpression(BinaryExpression expression) {
    TreeItem<AstTreeValue> operator =
        new TreeItem<>(
            new AstTreeValue(lineNumber(expression), () -> expression.operator().text()));

    operator.getChildren().add(expression.left().accept(this));
    operator.getChildren().add(expression.right().accept(this));

    return operator;
  }

  @Override
  public TreeItem<AstTreeValue> visitUnaryExpression(UnaryExpression expression) {
    TreeItem<AstTreeValue> operator =
        new TreeItem<>(
            new AstTreeValue(lineNumber(expression), () -> expression.operator().text()));

    operator.getChildren().add(expression.expression().accept(this));

    return operator;
  }

  @Override
  public TreeItem<AstTreeValue> visitGroupingExpression(GroupingExpression expression) {
    TreeItem<AstTreeValue> group =
        new TreeItem<>(new AstTreeValue(lineNumber(expression), () -> "Group"));
    group.getChildren().add(expression.expression().accept(this));

    return group;
  }

  @Override
  public TreeItem<AstTreeValue> visitNumberExpression(NumberExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(lineNumber(expression), () -> expression.token().text()));
  }

  @Override
  public TreeItem<AstTreeValue> visitStringExpression(StringExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(lineNumber(expression), () -> expression.token().text()));
  }

  @Override
  public TreeItem<AstTreeValue> visitIdentifierExpression(IdentifierExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(
            lineNumber(expression), () -> IdentifierUtil.normalize(expression.token().text())));
  }

  @Override
  public TreeItem<AstTreeValue> visitAddressExpression(AddressExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(lineNumber(expression), () -> expression.token().text()));
  }

  @Override
  public TreeItem<AstTreeValue> visitMacroCallExpression(MacroCallExpression expression) {
    TreeItem<AstTreeValue> macroCall =
        new TreeItem<>(new AstTreeValue(lineNumber(expression), () -> "MacroCall"));

    TreeItem<AstTreeValue> macro =
        new TreeItem<>(new AstTreeValue(lineNumber(expression), () -> "Macro"));
    macroCall.getChildren().add(macro);
    macro
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(lineNumber(expression), () -> expression.name().text())));

    if (!expression.arguments().isEmpty()) {
      TreeItem<AstTreeValue> arguments =
          new TreeItem<>(
              new AstTreeValue(expression.arguments().get(0).line().number(), () -> "Arguments"));
      macroCall.getChildren().add(arguments);
      expression
          .arguments()
          .forEach(argument -> arguments.getChildren().add(argument.accept(this)));
    }

    return macroCall;
  }

  @Override
  public TreeItem<AstTreeValue> visitRegisterOperand(RegisterOperand operand) {
    TreeItem<AstTreeValue> register =
        new TreeItem<>(
            new AstTreeValue(
                lineNumber(operand),
                () -> String.format("Register: %s", operand.register().name())));

    if (operand.displacement() != null) {
      TreeItem<AstTreeValue> displacement =
          new TreeItem<>(new AstTreeValue(lineNumber(operand), () -> "Displacement"));
      register.getChildren().add(displacement);
      displacement.getChildren().add(operand.displacement().accept(this));
    }

    return register;
  }

  @Override
  public TreeItem<AstTreeValue> visitExpressionOperand(ExpressionOperand operand) {
    return operand.expression().accept(this);
  }

  @Override
  public TreeItem<AstTreeValue> visitConditionOperand(ConditionOperand operand) {
    return new TreeItem<>(
        new AstTreeValue(
            lineNumber(operand), () -> String.format("Condition: %s", operand.condition().name())));
  }

  @Override
  public TreeItem<AstTreeValue> visitGroupingOperand(GroupingOperand operand) {
    TreeItem<AstTreeValue> group =
        new TreeItem<>(new AstTreeValue(lineNumber(operand), () -> "Group"));
    group.getChildren().add(operand.operand().accept(this));

    return group;
  }

  @Override
  public TreeItem<AstTreeValue> visitExpressionStatement(ExpressionStatement statement) {
    TreeItem<AstTreeValue> expression =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Expression"));
    expression.getChildren().add(statement.expression().accept(this));

    return expression;
  }

  @Override
  public TreeItem<AstTreeValue> visitInstructionStatement(InstructionStatement statement) {
    TreeItem<AstTreeValue> instruction =
        new TreeItem<>(
            new AstTreeValue(
                lineNumber(statement),
                () ->
                    String.format("Instruction: %s", Mnemonic.find(statement.mnemonic().text()))));
    if (!statement.operands().isEmpty()) {
      TreeItem<AstTreeValue> operands =
          new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Operands"));
      instruction.getChildren().add(operands);
      statement.operands().forEach(operand -> operands.getChildren().add(operand.accept(this)));
    }

    return instruction;
  }

  @Override
  public TreeItem<AstTreeValue> visitAssignStatement(AssignStatement statement) {
    TreeItem<AstTreeValue> assign =
        new TreeItem<>(
            new AstTreeValue(
                lineNumber(statement),
                () -> String.format("Assign (%s)", statement.type().name())));
    assign
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(
                    lineNumber(statement),
                    () -> IdentifierUtil.normalize(statement.identifier().text()))));
    assign.getChildren().add(statement.initializer().accept(this));

    return assign;
  }

  @Override
  public TreeItem<AstTreeValue> visitCommentStatement(CommentStatement statement) {
    return new TreeItem<>(
        new AstTreeValue(
            lineNumber(statement), () -> String.format("Comment: %s", statement.comment().text())));
  }

  @Override
  public TreeItem<AstTreeValue> visitEndStatement(EndStatement statement) {
    return new TreeItem<>(
        new AstTreeValue(lineNumber(statement), () -> String.format(statement.token().text())));
  }

  @Override
  public TreeItem<AstTreeValue> visitDataByteStatement(DataByteStatement statement) {
    TreeItem<AstTreeValue> dataByte =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Byte"));
    statement.values().forEach(value -> dataByte.getChildren().add(value.accept(this)));

    return dataByte;
  }

  @Override
  public TreeItem<AstTreeValue> visitDataWordStatement(DataWordStatement statement) {
    TreeItem<AstTreeValue> dataWord =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Word"));
    statement.values().forEach(value -> dataWord.getChildren().add(value.accept(this)));

    return dataWord;
  }

  @Override
  public TreeItem<AstTreeValue> visitDataLongStatement(DataLongStatement statement) {
    TreeItem<AstTreeValue> dataLong =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Long"));
    statement.values().forEach(value -> dataLong.getChildren().add(value.accept(this)));

    return dataLong;
  }

  @Override
  public TreeItem<AstTreeValue> visitDataTextStatement(DataTextStatement statement) {
    TreeItem<AstTreeValue> dataText =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Text"));
    statement.values().forEach(value -> dataText.getChildren().add(value.accept(this)));

    return dataText;
  }

  @Override
  public TreeItem<AstTreeValue> visitOriginStatement(OriginStatement statement) {
    TreeItem<AstTreeValue> origin =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Origin"));

    origin.getChildren().add(statement.location().accept(this));
    if (statement.fillByte() != null) {
      origin.getChildren().add(statement.fillByte().accept(this));
    }

    return origin;
  }

  @Override
  public TreeItem<AstTreeValue> visitAlignStatement(AlignStatement statement) {
    TreeItem<AstTreeValue> align =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Align"));

    align.getChildren().add(statement.alignment().accept(this));
    if (statement.fillByte() != null) {
      align.getChildren().add(statement.fillByte().accept(this));
    }

    return align;
  }

  @Override
  public TreeItem<AstTreeValue> visitBlockStatement(BlockStatement statement) {
    if (statement.statements().isEmpty()) {
      return null;
    }

    TreeItem<AstTreeValue> block =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Block"));

    block.getChildren().add(new SymbolTableTreeItem(statement.symbols()));

    statement.statements().forEach(s -> block.getChildren().add(s.accept(this)));

    return block;
  }

  @Override
  public TreeItem<AstTreeValue> visitLocalStatement(LocalStatement statement) {
    TreeItem<AstTreeValue> local =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Local"));

    local.getChildren().add(statement.block().accept(this));

    return local;
  }

  @Override
  public TreeItem<AstTreeValue> visitMacroStatement(MacroStatement statement) {
    TreeItem<AstTreeValue> macro =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Macro"));
    macro
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(statement.name().line().number(), () -> statement.name().text())));

    macro.getChildren().add(new SymbolTableTreeItem(statement.symbolTable()));

    if (!statement.parameters().isEmpty()) {
      TreeItem<AstTreeValue> parameters =
          new TreeItem<>(
              new AstTreeValue(
                  statement.parameters().get(0).name().line().number(), () -> "Parameters"));
      macro.getChildren().add(parameters);
      statement
          .parameters()
          .forEach(
              parameter ->
                  parameters
                      .getChildren()
                      .add(
                          new TreeItem<>(
                              new AstTreeValue(
                                  parameter.name().line().number(),
                                  () -> parameter.name().text()))));
    }

    macro.getChildren().add(statement.block().accept(this));

    return macro;
  }

  @Override
  public TreeItem<AstTreeValue> visitPhaseStatement(PhaseStatement statement) {
    TreeItem<AstTreeValue> phase =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Phase"));

    TreeItem<AstTreeValue> count =
        new TreeItem<>(new AstTreeValue(statement.expression().line().number(), () -> "Address"));
    phase.getChildren().add(count);
    count.getChildren().add(statement.expression().accept(this));
    phase.getChildren().add(statement.block().accept(this));

    return phase;
  }

  @Override
  public TreeItem<AstTreeValue> visitRepeatStatement(RepeatStatement statement) {
    TreeItem<AstTreeValue> repeat =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Repeat"));

    TreeItem<AstTreeValue> count =
        new TreeItem<>(new AstTreeValue(statement.count().line().number(), () -> "Count"));
    repeat.getChildren().add(count);
    count.getChildren().add(statement.count().accept(this));
    repeat.getChildren().add(statement.block().accept(this));

    return repeat;
  }

  @Override
  public TreeItem<AstTreeValue> visitConditionalStatement(ConditionalStatement statement) {
    TreeItem<AstTreeValue> condition =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "If"));

    TreeItem<AstTreeValue> expression =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Expression"));
    condition.getChildren().add(expression);
    expression.getChildren().add(statement.condition().accept(this));

    if (statement.thenBranch() != null) {
      TreeItem<AstTreeValue> then =
          new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Then"));
      condition.getChildren().add(then);
      then.getChildren().add(statement.thenBranch().accept(this));
    }

    if (statement.elseBranch() != null) {
      TreeItem<AstTreeValue> elseNode =
          new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Else"));
      condition.getChildren().add(elseNode);
      elseNode.getChildren().add(statement.elseBranch().accept(this));
    }

    return condition;
  }

  @Override
  public TreeItem<AstTreeValue> visitAssertStatement(AssertStatement statement) {
    TreeItem<AstTreeValue> assertion =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Label"));
    assertion.getChildren().add(statement.expression().accept(this));

    return assertion;
  }

  @Override
  public TreeItem<AstTreeValue> visitGlobalStatement(GlobalStatement statement) {
    return new TreeItem<>(
        new AstTreeValue(
            statement.identifier().line().number(),
            () -> IdentifierUtil.normalize(statement.identifier().text())));
  }

  @Override
  public TreeItem<AstTreeValue> visitMacroCallStatement(MacroCallStatement statement) {
    TreeItem<AstTreeValue> macroCall =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "MacroCall"));

    TreeItem<AstTreeValue> macro =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Macro"));
    macroCall.getChildren().add(macro);
    macro
        .getChildren()
        .add(
            new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> statement.name().text())));

    if (!statement.arguments().isEmpty()) {
      TreeItem<AstTreeValue> arguments =
          new TreeItem<>(
              new AstTreeValue(statement.arguments().get(0).line().number(), () -> "Arguments"));
      macroCall.getChildren().add(arguments);
      statement.arguments().forEach(argument -> arguments.getChildren().add(argument.accept(this)));
    }

    return macroCall;
  }

  @Override
  public TreeItem<AstTreeValue> visitEmptyStatement(EmptyStatement statement) {
    return null;
  }

  @Override
  public TreeItem<AstTreeValue> visitInsertStatement(InsertStatement statement) {
    TreeItem<AstTreeValue> insert =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Insert"));
    insert
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(lineNumber(statement), () -> statement.string().text())));

    return insert;
  }

  @Override
  public TreeItem<AstTreeValue> visitIncludeStatement(IncludeStatement statement) {
    TreeItem<AstTreeValue> insert =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Include"));
    insert
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(lineNumber(statement), () -> statement.string().text())));

    return insert;
  }

  private int lineNumber(Expression expression) {
    return lineNumber(expression.line());
  }

  private int lineNumber(Operand operand) {
    return lineNumber(operand.line());
  }

  private int lineNumber(Statement statement) {
    return lineNumber(statement.line());
  }

  private int lineNumber(Line line) {
    return line == null ? -1 : line.number();
  }

  public TreeItem<AstTreeValue> build(BlockStatement block) {
    return block.accept(this);
  }
}
