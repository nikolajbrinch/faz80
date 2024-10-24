package dk.nikolajbrinch.faz80.ide.ast;

import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;
import dk.nikolajbrinch.faz80.ide.symbols.SymbolTableTreeItem;
import dk.nikolajbrinch.faz80.parser.base.IdentifierNormalizer;
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
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import dk.nikolajbrinch.scanner.Line;
import javafx.scene.control.TreeItem;

public class AstTreeBuilder
    implements StatementProcessor<TreeItem<AstTreeValue>>,
        ExpressionProcessor<TreeItem<AstTreeValue>>,
        OperandProcessor<TreeItem<AstTreeValue>> {

  private final Logger logger = LoggerFactory.getLogger();

  @Override
  public TreeItem<AstTreeValue> processSectionStatement(SectionStatement statement) {
    TreeItem<AstTreeValue> section =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Section"));
    section
        .getChildren()
        .add(
            new TreeItem<>(
                new AstTreeValue(lineNumber(statement), () -> statement.name().text())));

    return section;
  }

  @Override
  public TreeItem<AstTreeValue> processBinaryExpression(BinaryExpression expression) {
    TreeItem<AstTreeValue> operator =
        new TreeItem<>(
            new AstTreeValue(lineNumber(expression), () -> expression.operator().text()));

    operator.getChildren().add(process(expression.left()));
    operator.getChildren().add(process(expression.right()));

    return operator;
  }

  @Override
  public TreeItem<AstTreeValue> processUnaryExpression(UnaryExpression expression) {
    TreeItem<AstTreeValue> operator =
        new TreeItem<>(
            new AstTreeValue(lineNumber(expression), () -> expression.operator().text()));

    operator.getChildren().add(process(expression.expression()));

    return operator;
  }

  @Override
  public TreeItem<AstTreeValue> processGroupingExpression(GroupingExpression expression) {
    TreeItem<AstTreeValue> group =
        new TreeItem<>(new AstTreeValue(lineNumber(expression), () -> "Group"));
    group.getChildren().add(process(expression.expression()));

    return group;
  }

  @Override
  public TreeItem<AstTreeValue> processNumberExpression(NumberExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(lineNumber(expression), () -> expression.token().text()));
  }

  @Override
  public TreeItem<AstTreeValue> processStringExpression(StringExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(lineNumber(expression), () -> expression.token().text()));
  }

  @Override
  public TreeItem<AstTreeValue> processIdentifierExpression(IdentifierExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(
            lineNumber(expression),
            () -> IdentifierNormalizer.normalize(expression.token().text())));
  }

  @Override
  public TreeItem<AstTreeValue> processAddressExpression(AddressExpression expression) {
    return new TreeItem<>(
        new AstTreeValue(lineNumber(expression), () -> expression.token().text()));
  }

  @Override
  public TreeItem<AstTreeValue> processMacroCallExpression(MacroCallExpression expression) {
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
      expression.arguments().forEach(argument -> arguments.getChildren().add(process(argument)));
    }

    return macroCall;
  }

  @Override
  public TreeItem<AstTreeValue> processRegisterOperand(RegisterOperand operand) {
    TreeItem<AstTreeValue> register =
        new TreeItem<>(
            new AstTreeValue(
                lineNumber(operand),
                () -> String.format("Register: %s", operand.register().name())));

    if (operand.displacement() != null) {
      TreeItem<AstTreeValue> displacement =
          new TreeItem<>(new AstTreeValue(lineNumber(operand), () -> "Displacement"));
      register.getChildren().add(displacement);
      displacement.getChildren().add(process(operand.displacement()));
    }

    return register;
  }

  @Override
  public TreeItem<AstTreeValue> processExpressionOperand(ExpressionOperand operand) {
    return process(operand.expression());
  }

  @Override
  public TreeItem<AstTreeValue> processConditionOperand(ConditionOperand operand) {
    return new TreeItem<>(
        new AstTreeValue(
            lineNumber(operand), () -> String.format("Condition: %s", operand.condition().name())));
  }

  @Override
  public TreeItem<AstTreeValue> processGroupingOperand(GroupingOperand operand) {
    TreeItem<AstTreeValue> group =
        new TreeItem<>(new AstTreeValue(lineNumber(operand), () -> "Group"));
    group.getChildren().add(process(operand.operand()));

    return group;
  }

  @Override
  public TreeItem<AstTreeValue> processExpressionStatement(ExpressionStatement statement) {
    TreeItem<AstTreeValue> expression =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Expression"));
    expression.getChildren().add(process(statement.expression()));

    return expression;
  }

  @Override
  public TreeItem<AstTreeValue> processInstructionStatement(InstructionStatement statement) {
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
      statement.operands().forEach(operand -> operands.getChildren().add(process(operand)));
    }

    return instruction;
  }

  @Override
  public TreeItem<AstTreeValue> processAssignStatement(AssignStatement statement) {
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
                    () -> IdentifierNormalizer.normalize(statement.identifier().text()))));
    assign.getChildren().add(process(statement.initializer()));

    return assign;
  }

  @Override
  public TreeItem<AstTreeValue> processCommentStatement(CommentStatement statement) {
    return new TreeItem<>(
        new AstTreeValue(
            lineNumber(statement), () -> String.format("Comment: %s", statement.comment().text())));
  }

  @Override
  public TreeItem<AstTreeValue> processEndStatement(EndStatement statement) {
    return new TreeItem<>(
        new AstTreeValue(lineNumber(statement), () -> String.format(statement.token().text())));
  }

  @Override
  public TreeItem<AstTreeValue> processDataByteStatement(DataByteStatement statement) {
    TreeItem<AstTreeValue> dataByte =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Byte"));
    statement.values().forEach(value -> dataByte.getChildren().add(process(value)));

    return dataByte;
  }

  @Override
  public TreeItem<AstTreeValue> processDataWordStatement(DataWordStatement statement) {
    TreeItem<AstTreeValue> dataWord =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Word"));
    statement.values().forEach(value -> dataWord.getChildren().add(process(value)));

    return dataWord;
  }

  @Override
  public TreeItem<AstTreeValue> processDataLongStatement(DataLongStatement statement) {
    TreeItem<AstTreeValue> dataLong =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Long"));
    statement.values().forEach(value -> dataLong.getChildren().add(process(value)));

    return dataLong;
  }

  @Override
  public TreeItem<AstTreeValue> processDataTextStatement(DataTextStatement statement) {
    TreeItem<AstTreeValue> dataText =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Text"));
    statement.values().forEach(value -> dataText.getChildren().add(process(value)));

    return dataText;
  }

  @Override
  public TreeItem<AstTreeValue> processOriginStatement(OriginStatement statement) {
    TreeItem<AstTreeValue> origin =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Origin"));

    origin.getChildren().add(process(statement.location()));
    if (statement.fillByte() != null) {
      origin.getChildren().add(process(statement.fillByte()));
    }

    return origin;
  }

  @Override
  public TreeItem<AstTreeValue> processAlignStatement(AlignStatement statement) {
    TreeItem<AstTreeValue> align =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Align"));

    align.getChildren().add(process(statement.alignment()));
    if (statement.fillByte() != null) {
      align.getChildren().add(process(statement.fillByte()));
    }

    return align;
  }

  @Override
  public TreeItem<AstTreeValue> processBlockStatement(BlockStatement statement) {
    if (statement.statements().isEmpty()) {
      return null;
    }

    TreeItem<AstTreeValue> block =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Block"));

    block.getChildren().add(new SymbolTableTreeItem(statement.symbols()));

    statement.statements().forEach(s -> block.getChildren().add(process(s)));

    return block;
  }

  @Override
  public TreeItem<AstTreeValue> processLocalStatement(LocalStatement statement) {
    TreeItem<AstTreeValue> local =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Local"));

    local.getChildren().add(process(statement.block()));

    return local;
  }

  @Override
  public TreeItem<AstTreeValue> processMacroStatement(MacroStatement statement) {
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

    macro.getChildren().add(process(statement.block()));

    return macro;
  }

  @Override
  public TreeItem<AstTreeValue> processPhaseStatement(PhaseStatement statement) {
    TreeItem<AstTreeValue> phase =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Phase"));

    TreeItem<AstTreeValue> count =
        new TreeItem<>(new AstTreeValue(statement.expression().line().number(), () -> "Address"));
    phase.getChildren().add(count);
    count.getChildren().add(process(statement.expression()));
    phase.getChildren().add(process(statement.block()));

    return phase;
  }

  @Override
  public TreeItem<AstTreeValue> processRepeatStatement(RepeatStatement statement) {
    TreeItem<AstTreeValue> repeat =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Repeat"));

    TreeItem<AstTreeValue> count =
        new TreeItem<>(new AstTreeValue(statement.count().line().number(), () -> "Count"));
    repeat.getChildren().add(count);
    count.getChildren().add(process(statement.count()));
    repeat.getChildren().add(process(statement.block()));

    return repeat;
  }

  @Override
  public TreeItem<AstTreeValue> processConditionalStatement(ConditionalStatement statement) {
    TreeItem<AstTreeValue> condition =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "If"));

    TreeItem<AstTreeValue> expression =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Expression"));
    condition.getChildren().add(expression);
    expression.getChildren().add(process(statement.condition()));

    if (statement.thenBranch() != null) {
      TreeItem<AstTreeValue> then =
          new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Then"));
      condition.getChildren().add(then);
      then.getChildren().add(process(statement.thenBranch()));
    }

    if (statement.elseBranch() != null) {
      TreeItem<AstTreeValue> elseNode =
          new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Else"));
      condition.getChildren().add(elseNode);
      elseNode.getChildren().add(process(statement.elseBranch()));
    }

    return condition;
  }

  @Override
  public TreeItem<AstTreeValue> processAssertStatement(AssertStatement statement) {
    TreeItem<AstTreeValue> assertion =
        new TreeItem<>(new AstTreeValue(lineNumber(statement), () -> "Label"));
    assertion.getChildren().add(process(statement.expression()));

    return assertion;
  }

  @Override
  public TreeItem<AstTreeValue> processGlobalStatement(GlobalStatement statement) {
    return new TreeItem<>(
        new AstTreeValue(
            statement.identifier().line().number(),
            () -> IdentifierNormalizer.normalize(statement.identifier().text())));
  }

  @Override
  public TreeItem<AstTreeValue> processMacroCallStatement(MacroCallStatement statement) {
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
      statement.arguments().forEach(argument -> arguments.getChildren().add(process(argument)));
    }

    return macroCall;
  }

  @Override
  public TreeItem<AstTreeValue> processEmptyStatement(EmptyStatement statement) {
    return null;
  }

  @Override
  public TreeItem<AstTreeValue> processInsertStatement(InsertStatement statement) {
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
  public TreeItem<AstTreeValue> processIncludeStatement(IncludeStatement statement) {
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
    long currentTime = System.currentTimeMillis();

    try {
      return process(block);
    } finally{
      logger.debug("Building AST took: " + (System.currentTimeMillis() - currentTime) + "ms");
    }
  }
}
