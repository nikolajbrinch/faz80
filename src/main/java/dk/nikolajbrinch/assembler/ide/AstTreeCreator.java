package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.ast.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.ast.expressions.AssignExpression;
import dk.nikolajbrinch.assembler.ast.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.ast.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.ast.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.ast.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.ast.expressions.LiteralExpression;
import dk.nikolajbrinch.assembler.ast.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.ast.operands.ConditionOperand;
import dk.nikolajbrinch.assembler.ast.operands.ExpressionOperand;
import dk.nikolajbrinch.assembler.ast.operands.GroupingOperand;
import dk.nikolajbrinch.assembler.ast.operands.OperandVisitor;
import dk.nikolajbrinch.assembler.ast.operands.RegisterOperand;
import dk.nikolajbrinch.assembler.ast.statements.AlignStatement;
import dk.nikolajbrinch.assembler.ast.statements.AssertStatement;
import dk.nikolajbrinch.assembler.ast.statements.BlockStatement;
import dk.nikolajbrinch.assembler.ast.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.ast.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.ast.statements.DataByteStatement;
import dk.nikolajbrinch.assembler.ast.statements.DataLongStatement;
import dk.nikolajbrinch.assembler.ast.statements.DataTextStatement;
import dk.nikolajbrinch.assembler.ast.statements.DataWordStatement;
import dk.nikolajbrinch.assembler.ast.statements.EmptyStatement;
import dk.nikolajbrinch.assembler.ast.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.ast.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.ast.statements.InsertStatement;
import dk.nikolajbrinch.assembler.ast.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.ast.statements.LabelStatement;
import dk.nikolajbrinch.assembler.ast.statements.LocalStatement;
import dk.nikolajbrinch.assembler.ast.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.ast.statements.MacroStatement;
import dk.nikolajbrinch.assembler.ast.statements.OriginStatement;
import dk.nikolajbrinch.assembler.ast.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.ast.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.ast.statements.StatementVisitor;
import dk.nikolajbrinch.assembler.ast.statements.VariableStatement;
import javafx.scene.control.TreeItem;

public class AstTreeCreator
    implements StatementVisitor<TreeItem<AstTreeValue>>,
        ExpressionVisitor<TreeItem<AstTreeValue>>,
        OperandVisitor<TreeItem<AstTreeValue>> {

  @Override
  public TreeItem<AstTreeValue> visitBinaryExpression(BinaryExpression expression) {
    TreeItem<AstTreeValue> operator =
        new TreeItem<>(new AstTreeValue(expression.line().number(), expression.operator().text()));

    operator.getChildren().add(expression.left().accept(this));
    operator.getChildren().add(expression.right().accept(this));

    return operator;
  }

  @Override
  public TreeItem<AstTreeValue> visitUnaryExpression(UnaryExpression expression) {
    TreeItem<AstTreeValue> operator =
        new TreeItem<>(new AstTreeValue(expression.line().number(), expression.operator().text()));

    operator.getChildren().add(expression.expression().accept(this));

    return operator;
  }

  @Override
  public TreeItem<AstTreeValue> visitGroupingExpression(GroupingExpression expression) {
    TreeItem<AstTreeValue> group =
        new TreeItem<>(new AstTreeValue(expression.line().number(), "Group"));
    group.getChildren().add(expression.accept(this));

    return group;
  }

  @Override
  public TreeItem<AstTreeValue> visitLiteralExpression(LiteralExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(
            expression.line().number(),
            switch (expression.token().type()) {
              case HEX_NUMBER -> "0x" + expression.token().text();
              case BINARY_NUMBER -> "0b" + expression.token().text();
              case OCTAL_NUMBER -> "0" + expression.token().text();
              default -> expression.token().text();
            }));
  }

  @Override
  public TreeItem<AstTreeValue> visitIdentifierExpression(IdentifierExpression expression) {
    return new TreeItem<>(new AstTreeValue(expression.line().number(), expression.token().text()));
  }

  @Override
  public TreeItem<AstTreeValue> visitAddressExpression(AddressExpression expression) {
    return new TreeItem<>(new AstTreeValue(expression.line().number(), expression.token().text()));
  }

  @Override
  public TreeItem<AstTreeValue> visitAssignExpression(AssignExpression expression) {
    TreeItem<AstTreeValue> assign =
        new TreeItem<>(new AstTreeValue(expression.line().number(), "Assign"));
    assign
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(expression.line().number(), expression.identifier().text())));
    assign.getChildren().add(expression.expression().accept(this));

    return assign;
  }

  @Override
  public TreeItem<AstTreeValue> visitRegisterOperand(RegisterOperand operand) {
    TreeItem<AstTreeValue> register =
        new TreeItem<>(new AstTreeValue(operand.line().number(), "Register"));

    register
        .getChildren()
        .add(new TreeItem<>(new AstTreeValue(operand.line().number(), operand.register().name())));
    if (operand.displacement() != null) {
      register.getChildren().add(operand.displacement().accept(this));
    }

    return register;
  }

  @Override
  public TreeItem<AstTreeValue> visitExpressionOperand(ExpressionOperand operand) {
    return operand.expression().accept(this);
  }

  @Override
  public TreeItem<AstTreeValue> visitConditionOperand(ConditionOperand operand) {
    return new TreeItem<>(new AstTreeValue(operand.line().number(), operand.condition().name()));
  }

  @Override
  public TreeItem<AstTreeValue> visitGroupingOperand(GroupingOperand operand) {
    TreeItem<AstTreeValue> group =
        new TreeItem<>(new AstTreeValue(operand.line().number(), "Group"));
    group.getChildren().add(operand.operand().accept(this));

    return group;
  }

  @Override
  public TreeItem<AstTreeValue> visitExpressionStatement(ExpressionStatement statement) {
    TreeItem<AstTreeValue> expression =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Expression"));
    expression.getChildren().add(statement.expression().accept(this));

    return expression;
  }

  @Override
  public TreeItem<AstTreeValue> visitInstructionStatement(InstructionStatement statement) {
    TreeItem<AstTreeValue> instruction =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Instruction"));
    TreeItem<AstTreeValue> opcode =
        new TreeItem<>(new AstTreeValue(statement.line().number(), statement.mnemonic().text()));
    instruction.getChildren().add(opcode);
    if (statement.operand1() != null) {
      opcode.getChildren().add(statement.operand1().accept(this));
    }
    if (statement.operand2() != null) {
      opcode.getChildren().add(statement.operand2().accept(this));
    }

    return instruction;
  }

  @Override
  public TreeItem<AstTreeValue> visitConstantStatement(ConstantStatement statement) {
    TreeItem<AstTreeValue> assign =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Constant"));
    assign
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(statement.line().number(), statement.identifier().text())));
    assign.getChildren().add(statement.value().accept(this));

    return assign;
  }

  @Override
  public TreeItem<AstTreeValue> visitVariableStatement(VariableStatement statement) {
    TreeItem<AstTreeValue> veriable =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Variable"));
    veriable
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(statement.line().number(), statement.identifier().text())));
    veriable.getChildren().add(statement.intializer().accept(this));

    return veriable;
  }

  @Override
  public TreeItem<AstTreeValue> visitDataByteStatement(DataByteStatement statement) {
    TreeItem<AstTreeValue> dataByte =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Byte"));
    statement.values().forEach(value -> dataByte.getChildren().add(value.accept(this)));

    return dataByte;
  }

  @Override
  public TreeItem<AstTreeValue> visitDataWordStatement(DataWordStatement statement) {
    TreeItem<AstTreeValue> dataWord =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Word"));
    statement.values().forEach(value -> dataWord.getChildren().add(value.accept(this)));

    return dataWord;
  }

  @Override
  public TreeItem<AstTreeValue> visitDataLongStatement(DataLongStatement statement) {
    TreeItem<AstTreeValue> dataLong =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Long"));
    statement.values().forEach(value -> dataLong.getChildren().add(value.accept(this)));

    return dataLong;
  }

  @Override
  public TreeItem<AstTreeValue> visitDataTextStatement(DataTextStatement statement) {
    TreeItem<AstTreeValue> dataText =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Text"));
    statement.values().forEach(value -> dataText.getChildren().add(value.accept(this)));

    return dataText;
  }

  @Override
  public TreeItem<AstTreeValue> visitOriginStatement(OriginStatement statement) {
    TreeItem<AstTreeValue> origin =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Origin"));

    origin.getChildren().add(statement.location().accept(this));
    if (statement.fillByte() != null) {
      origin.getChildren().add(statement.fillByte().accept(this));
    }

    return origin;
  }

  @Override
  public TreeItem<AstTreeValue> visitAlignStatement(AlignStatement statement) {
    TreeItem<AstTreeValue> align =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Align"));

    align.getChildren().add(statement.alignment().accept(this));
    align.getChildren().add(statement.fillByte().accept(this));

    return align;
  }

  @Override
  public TreeItem<AstTreeValue> visitBlockStatement(BlockStatement statement) {
    TreeItem<AstTreeValue> block =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Block"));

    block.getChildren().add(new SymbolTableTreeItem(statement.symbolTable()));

    statement.statements().forEach(s -> block.getChildren().add(s.accept(this)));

    return block;
  }

  @Override
  public TreeItem<AstTreeValue> visitLocalStatement(LocalStatement statement) {
    TreeItem<AstTreeValue> local =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Local"));

    local.getChildren().add(statement.block().accept(this));

    return local;
  }

  @Override
  public TreeItem<AstTreeValue> visitMacroStatement(MacroStatement statement) {
    TreeItem<AstTreeValue> macro =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Macro"));
    macro
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(statement.name().line().number(), statement.name().text())));

    macro.getChildren().add(new SymbolTableTreeItem(statement.symbolTable()));

    if (!statement.parameters().isEmpty()) {
      TreeItem<AstTreeValue> parameters =
          new TreeItem<>(
              new AstTreeValue(statement.parameters().get(0).name().line().number(), "Parameters"));
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
                                  parameter.name().line().number(), parameter.name().text()))));
    }

    macro.getChildren().add(statement.block().accept(this));

    return macro;
  }

  @Override
  public TreeItem<AstTreeValue> visitPhaseStatement(PhaseStatement statement) {
    TreeItem<AstTreeValue> phase =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Phase"));

    TreeItem<AstTreeValue> count =
        new TreeItem<>(new AstTreeValue(statement.expression().line().number(), "Address"));
    phase.getChildren().add(count);
    count.getChildren().add(statement.expression().accept(this));

    TreeItem<AstTreeValue> block =
        new TreeItem<>(new AstTreeValue(statement.block().line().number(), "Block"));
    phase.getChildren().add(block);
    block.getChildren().add(statement.block().accept(this));

    return phase;
  }

  @Override
  public TreeItem<AstTreeValue> visitRepeatStatement(RepeatStatement statement) {
    TreeItem<AstTreeValue> repeat =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Repeat"));

    TreeItem<AstTreeValue> count =
        new TreeItem<>(new AstTreeValue(statement.count().line().number(), "Count"));
    repeat.getChildren().add(count);
    count.getChildren().add(statement.count().accept(this));

    TreeItem<AstTreeValue> block =
        new TreeItem<>(new AstTreeValue(statement.blockStatement().line().number(), "Block"));
    repeat.getChildren().add(block);
    block.getChildren().add(statement.blockStatement().accept(this));

    return repeat;
  }

  @Override
  public TreeItem<AstTreeValue> visitConditionalStatement(ConditionalStatement statement) {
    TreeItem<AstTreeValue> condition = statement.accept(this);

    condition.getChildren().add(statement.thenBranch().accept(this));
    condition.getChildren().add(statement.elseBranch().accept(this));

    return condition;
  }

  @Override
  public TreeItem<AstTreeValue> visitAssertStatement(AssertStatement statement) {
    TreeItem<AstTreeValue> assertion =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Label"));
    assertion.getChildren().add(statement.expression().accept(this));

    return assertion;
  }

  @Override
  public TreeItem<AstTreeValue> visitGlobalStatement(GlobalStatement statement) {
    return new TreeItem<>(
        new AstTreeValue(statement.identifier().line().number(), statement.identifier().text()));
  }

  @Override
  public TreeItem<AstTreeValue> visitLabelStatement(LabelStatement statement) {
    TreeItem<AstTreeValue> label =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Label"));
    label
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(
                    statement.identifier().line().number(), statement.identifier().text())));

    return label;
  }

  @Override
  public TreeItem<AstTreeValue> visitMacroCallStatement(MacroCallStatement statement) {
    TreeItem<AstTreeValue> macroCall =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "MacroCall"));

    TreeItem<AstTreeValue> macro =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Macro"));
    macroCall.getChildren().add(macro);
    macro
        .getChildren()
        .add(new TreeItem<>(new AstTreeValue(statement.line().number(), statement.name().text())));

    if (!statement.arguments().isEmpty()) {
      TreeItem<AstTreeValue> arguments =
          new TreeItem<>(
              new AstTreeValue(statement.arguments().get(0).line().number(), "Arguments"));
      macroCall.getChildren().add(arguments);
      statement.arguments().forEach(argument -> arguments.getChildren().add(argument.accept(this)));
    }

    return macroCall;
  }

  @Override
  public TreeItem<AstTreeValue> visitEmptyStatement(EmptyStatement statement) {
    return new TreeItem<>(new AstTreeValue(statement.line().number(), statement));
  }

  @Override
  public TreeItem<AstTreeValue> visitInsertStatement(InsertStatement statement) {
    TreeItem<AstTreeValue> insert =
        new TreeItem<>(new AstTreeValue(statement.line().number(), "Insert"));
    insert
        .getChildren()
        .add(
            new TreeItem<>(new AstTreeValue(statement.line().number(), statement.string().text())));

    return insert;
  }

  public TreeItem<AstTreeValue> createTree(BlockStatement block) {
    return block.accept(this);
  }
}
