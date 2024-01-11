package dk.nikolajbrinch.assembler.ast;

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
import dk.nikolajbrinch.assembler.ast.statements.DataByteStatement;
import dk.nikolajbrinch.assembler.ast.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.ast.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.ast.statements.DataTextStatement;
import dk.nikolajbrinch.assembler.ast.statements.EmptyStatement;
import dk.nikolajbrinch.assembler.ast.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.ast.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.ast.statements.InsertStatement;
import dk.nikolajbrinch.assembler.ast.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.ast.statements.LabelStatement;
import dk.nikolajbrinch.assembler.ast.statements.LocalStatement;
import dk.nikolajbrinch.assembler.ast.statements.DataLongStatement;
import dk.nikolajbrinch.assembler.ast.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.ast.statements.MacroStatement;
import dk.nikolajbrinch.assembler.ast.statements.OriginStatement;
import dk.nikolajbrinch.assembler.ast.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.ast.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.ast.statements.StatementVisitor;
import dk.nikolajbrinch.assembler.ast.statements.VariableStatement;
import dk.nikolajbrinch.assembler.ast.statements.DataWordStatement;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

public class AstTreeCreator
    implements StatementVisitor<DefaultMutableTreeNode>,
        ExpressionVisitor<DefaultMutableTreeNode>,
        OperandVisitor<DefaultMutableTreeNode> {

  @Override
  public DefaultMutableTreeNode visitBinaryExpression(BinaryExpression expression) {
    DefaultMutableTreeNode operator = new DefaultMutableTreeNode(expression.operator().text());

    operator.add(expression.left().accept(this));
    operator.add(expression.right().accept(this));

    return operator;
  }

  @Override
  public DefaultMutableTreeNode visitUnaryExpression(UnaryExpression expression) {
    DefaultMutableTreeNode operator = new DefaultMutableTreeNode(expression.operator().text());

    operator.add(expression.expression().accept(this));

    return operator;
  }

  @Override
  public DefaultMutableTreeNode visitGroupingExpression(GroupingExpression expression) {
    DefaultMutableTreeNode group = new DefaultMutableTreeNode("Group");
    group.add(expression.accept(this));

    return group;
  }

  @Override
  public DefaultMutableTreeNode visitLiteralExpression(LiteralExpression expression) {
    return new DefaultMutableTreeNode(
        switch (expression.token().type()) {
          case HEX_NUMBER -> "0x" + expression.token().text();
          case BINARY_NUMBER -> "0b" + expression.token().text();
          case OCTAL_NUMBER -> "0" + expression.token().text();
          default -> expression.token().text();
        });
  }

  @Override
  public DefaultMutableTreeNode visitIdentifierExpression(IdentifierExpression expression) {
    return new DefaultMutableTreeNode(expression.token().text());
  }

  @Override
  public DefaultMutableTreeNode visitAddressExpression(AddressExpression expression) {
    return new DefaultMutableTreeNode(expression.token().text());
  }

  @Override
  public DefaultMutableTreeNode visitAssignExpression(AssignExpression expression) {
    DefaultMutableTreeNode assign = new DefaultMutableTreeNode("Assign");
    assign.add(new DefaultMutableTreeNode(expression.identifier().text()));
    assign.add(expression.expression().accept(this));

    return assign;
  }

  @Override
  public DefaultMutableTreeNode visitRegisterOperand(RegisterOperand operand) {
    DefaultMutableTreeNode register = new DefaultMutableTreeNode("Register");

    register.add(new DefaultMutableTreeNode(operand.register().name()));
    if (operand.displacement() != null) {
      register.add(operand.displacement().accept(this));
    }

    return register;
  }

  @Override
  public DefaultMutableTreeNode visitExpressionOperand(ExpressionOperand operand) {
    return operand.expression().accept(this);
  }

  @Override
  public DefaultMutableTreeNode visitConditionOperand(ConditionOperand operand) {
    return new DefaultMutableTreeNode(operand.condition().name());
  }

  @Override
  public DefaultMutableTreeNode visitGroupingOperand(GroupingOperand operand) {
    DefaultMutableTreeNode group = new DefaultMutableTreeNode("Group");
    group.add(operand.operand().accept(this));

    return group;
  }

  @Override
  public DefaultMutableTreeNode visitExpressionStatement(ExpressionStatement statement) {
    DefaultMutableTreeNode expression = new DefaultMutableTreeNode("Expression");
    expression.add(statement.expression().accept(this));

    return expression;
  }

  @Override
  public DefaultMutableTreeNode visitInstructionStatement(InstructionStatement statement) {
    DefaultMutableTreeNode instruction = new DefaultMutableTreeNode("Instruction");
    DefaultMutableTreeNode opcode = new DefaultMutableTreeNode(statement.mnemonic().text());
    instruction.add(opcode);
    if (statement.operand1() != null) {
      opcode.add(statement.operand1().accept(this));
    }
    if (statement.operand2() != null) {
      opcode.add(statement.operand2().accept(this));
    }

    return instruction;
  }

  @Override
  public DefaultMutableTreeNode visitConstantStatement(ConstantStatement statement) {
    DefaultMutableTreeNode assign = new DefaultMutableTreeNode("Constant");
    assign.add(new DefaultMutableTreeNode(statement.identifier().text()));
    assign.add(statement.value().accept(this));

    return assign;
  }

  @Override
  public DefaultMutableTreeNode visitVariableStatement(VariableStatement statement) {
    DefaultMutableTreeNode veriable = new DefaultMutableTreeNode("Variable");
    veriable.add(new DefaultMutableTreeNode(statement.identifier().text()));
    veriable.add(statement.intializer().accept(this));

    return veriable;
  }

  @Override
  public DefaultMutableTreeNode visitDataByteStatement(DataByteStatement statement) {
    DefaultMutableTreeNode dataByte = new DefaultMutableTreeNode("Byte");
    statement.values().forEach(value -> dataByte.add(value.accept(this)));

    return dataByte;
  }

  @Override
  public DefaultMutableTreeNode visitDataWordStatement(DataWordStatement statement) {
    DefaultMutableTreeNode dataWord = new DefaultMutableTreeNode("Word");
    statement.values().forEach(value -> dataWord.add(value.accept(this)));

    return dataWord;
  }

  @Override
  public DefaultMutableTreeNode visitDataLongStatement(DataLongStatement statement) {
    DefaultMutableTreeNode dataLong = new DefaultMutableTreeNode("Long");
    statement.values().forEach(value -> dataLong.add(value.accept(this)));

    return dataLong;
  }

  @Override
  public DefaultMutableTreeNode visitDataTextStatement(DataTextStatement statement) {
    DefaultMutableTreeNode dataText = new DefaultMutableTreeNode("Text");
    statement.values().forEach(value -> dataText.add(value.accept(this)));

    return dataText;
  }

  @Override
  public DefaultMutableTreeNode visitOriginStatement(OriginStatement statement) {
    DefaultMutableTreeNode origin = new DefaultMutableTreeNode("Origin");

    origin.add(statement.location().accept(this));
    if (statement.fillByte() != null) {
      origin.add(statement.fillByte().accept(this));
    }

    return origin;
  }

  @Override
  public DefaultMutableTreeNode visitAlignStatement(AlignStatement statement) {
    DefaultMutableTreeNode align = new DefaultMutableTreeNode("Origin");

    align.add(statement.alignment().accept(this));
    align.add(statement.fillByte().accept(this));

    return align;
  }

  @Override
  public DefaultMutableTreeNode visitBlockStatement(BlockStatement statement) {
    DefaultMutableTreeNode block = new DefaultMutableTreeNode("block");

    statement.statements().forEach(s -> block.add(s.accept(this)));

    return block;
  }

  @Override
  public DefaultMutableTreeNode visitLocalStatement(LocalStatement statement) {
    DefaultMutableTreeNode local = new DefaultMutableTreeNode("Local");

    local.add(statement.block().accept(this));

    return local;
  }

  @Override
  public DefaultMutableTreeNode visitMacroStatement(MacroStatement statement) {
    DefaultMutableTreeNode macro = new DefaultMutableTreeNode("Macro");
    macro.add(new DefaultMutableTreeNode(statement.name().text()));

    DefaultMutableTreeNode parameters = new DefaultMutableTreeNode("Parameters");
    statement
        .parameters()
        .forEach(parameter -> parameters.add(new DefaultMutableTreeNode(parameter.name().text())));

    macro.add(statement.block().accept(this));

    return macro;
  }

  @Override
  public DefaultMutableTreeNode visitPhaseStatement(PhaseStatement statement) {
    DefaultMutableTreeNode phase = new DefaultMutableTreeNode("Phase");

    DefaultMutableTreeNode count = new DefaultMutableTreeNode("Address");
    phase.add(count);
    count.add(statement.expression().accept(this));

    DefaultMutableTreeNode block = new DefaultMutableTreeNode("Block");
    phase.add(block);
    block.add(statement.block().accept(this));

    return phase;  }

  @Override
  public DefaultMutableTreeNode visitRepeatStatement(RepeatStatement statement) {
    DefaultMutableTreeNode repeat = new DefaultMutableTreeNode("Repeat");

    DefaultMutableTreeNode count = new DefaultMutableTreeNode("Count");
    repeat.add(count);
    count.add(statement.count().accept(this));

    DefaultMutableTreeNode block = new DefaultMutableTreeNode("Block");
    repeat.add(block);
    block.add(statement.blockStatement().accept(this));

    return repeat;
  }

  @Override
  public DefaultMutableTreeNode visitConditionalStatement(
      ConditionalStatement conditionalStatement) {
    DefaultMutableTreeNode condition = conditionalStatement.accept(this);

    condition.add(conditionalStatement.thenBranch().accept(this));
    condition.add(conditionalStatement.elseBranch().accept(this));

    return condition;
  }

  @Override
  public DefaultMutableTreeNode visitAssertStatement(AssertStatement assertStatement) {
    DefaultMutableTreeNode assertion = new DefaultMutableTreeNode("Label");
    assertion.add(assertStatement.expression().accept(this));

    return assertion;
  }

  @Override
  public DefaultMutableTreeNode visitGlobalStatement(GlobalStatement globalStatement) {
    return new DefaultMutableTreeNode(globalStatement.identifier().text());
  }

  @Override
  public DefaultMutableTreeNode visitLabelStatement(LabelStatement labelStatement) {
    DefaultMutableTreeNode label = new DefaultMutableTreeNode("Label");
    label.add(new DefaultMutableTreeNode(labelStatement.identifier().text()));

    return label;
  }

  @Override
  public DefaultMutableTreeNode visitMacroCallStatement(MacroCallStatement macroCallStatement) {
    DefaultMutableTreeNode macroCall = new DefaultMutableTreeNode("MacroCall");

    DefaultMutableTreeNode macro = new DefaultMutableTreeNode("Macro");
    macroCall.add(macro);
    macro.add(new DefaultMutableTreeNode(macroCallStatement.name().text()));

    DefaultMutableTreeNode arguments = new DefaultMutableTreeNode("Arguments");
    macroCall.add(arguments);
    macroCallStatement.arguments().forEach(argument -> arguments.add(argument.accept(this)));

    return macroCall;
  }

  @Override
  public DefaultMutableTreeNode visitEmptyStatement(EmptyStatement emptyStatement) {
    return new DefaultMutableTreeNode(emptyStatement);
  }

  @Override
  public DefaultMutableTreeNode visitInsertStatement(InsertStatement insertStatement) {
    DefaultMutableTreeNode insert = new DefaultMutableTreeNode("Insert");
    insert.add(new DefaultMutableTreeNode(insertStatement.string().text()));

    return insert;
  }


  public DefaultMutableTreeNode createTree(List<Statement> statements) {
    DefaultMutableTreeNode ast = new DefaultMutableTreeNode("AST");

    statements.forEach(statement -> ast.add(statement.accept(this)));

    return ast;
  }
}
