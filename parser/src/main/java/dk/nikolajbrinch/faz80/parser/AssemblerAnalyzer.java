package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.expressions.NoOpExpressionProcessor;
import dk.nikolajbrinch.faz80.parser.operands.NoOpOperandProcessor;
import dk.nikolajbrinch.faz80.parser.operands.Operand;
import dk.nikolajbrinch.faz80.parser.statements.BlockStatement;
import dk.nikolajbrinch.faz80.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.faz80.parser.statements.InstructionStatement;
import dk.nikolajbrinch.faz80.parser.statements.LocalStatement;
import dk.nikolajbrinch.faz80.parser.statements.MacroCallStatement;
import dk.nikolajbrinch.faz80.parser.statements.MacroStatement;
import dk.nikolajbrinch.faz80.parser.statements.NoOpStatementProcessor;
import dk.nikolajbrinch.faz80.parser.statements.PhaseStatement;
import dk.nikolajbrinch.faz80.parser.statements.RepeatStatement;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import dk.nikolajbrinch.parser.ParseMessage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class AssemblerAnalyzer
    implements NoOpStatementProcessor, NoOpExpressionProcessor, NoOpOperandProcessor {

  private final InstructionFixer instructionFixer = new InstructionFixer();

  private Deque<SymbolTable> symbolTables = new ArrayDeque<>();

  private List<ParseMessage> errors;

  public AssemblerParseResult analyze(AssemblerParseResult parseResult) {
    this.errors = parseResult.messages();

    Statement statement = process(parseResult.block());

    return new AssemblerParseResult((BlockStatement) statement, errors);
  }

  @Override
  public Statement processInstructionStatement(InstructionStatement statement) {
    AssemblerToken mnemonic = statement.mnemonic();

    Mnemonic instruction = Mnemonic.find(mnemonic.text());

    List<Operand> operands = processOperands(statement.operands());

    if (operands.size() < instruction.getOperandsLowerBound()) {
      InstructionStatement fixedInstruction =
          instructionFixer.fixInstruction(instruction, mnemonic, operands, errors);

      if (fixedInstruction != null) {
        return fixedInstruction;
      }

      errors.add(
          ParseMessage.error(
              mnemonic,
              "Instruction "
                  + instruction
                  + " requires at least "
                  + instruction.getOperandsLowerBound()
                  + " operands"));
    } else if (operands.size() > instruction.getOperandsUpperBound()) {
      InstructionStatement fixedInstruction =
          instructionFixer.fixInstruction(instruction, mnemonic, operands, errors);

      if (fixedInstruction != null) {
        return fixedInstruction;
      }

      errors.add(
          ParseMessage.error(
              mnemonic,
              "Instruction "
                  + instruction
                  + " accepts maximum "
                  + instruction.getOperandsLowerBound()
                  + " operands"));
    }

    return new InstructionStatement(mnemonic, operands);
  }

  @Override
  public Statement processBlockStatement(BlockStatement statement) {
    return copyBlock(statement);
  }

  @Override
  public Statement processLocalStatement(LocalStatement statement) {
    return new LocalStatement(
        statement.startToken(), statement.endToken(), copyBlock(statement.block()));
  }

  @Override
  public Statement processMacroStatement(MacroStatement statement) {
    return new MacroStatement(
        statement.startToken(),
        statement.endToken(),
        statement.name(),
        statement.symbolTable(),
        processParameters(statement.parameters()),
        copyBlock(statement.block()));
  }

  @Override
  public Statement processPhaseStatement(PhaseStatement statement) {
    return new PhaseStatement(
        statement.startToken(),
        statement.endToken(),
        process(statement.expression()),
        copyBlock(statement.block()));
  }

  @Override
  public Statement processRepeatStatement(RepeatStatement statement) {
    return new RepeatStatement(
        statement.startToken(),
        statement.endToken(),
        process(statement.count()),
        copyBlock(statement.block()));
  }

  @Override
  public Statement processMacroCallStatement(MacroCallStatement statement) {
    return new MacroCallStatement(statement.name(), processStatements(statement.arguments()));
  }

  @Override
  public Statement processConditionalStatement(ConditionalStatement statement) {
    return new ConditionalStatement(
        statement.ifToken(),
        statement.elseToken(),
        statement.endToken(),
        process(statement.condition()),
        process(statement.thenBranch()),
        process(statement.elseBranch()));
  }

  private BlockStatement copyBlock(BlockStatement statement) {
    try {
      symbolTables.push(statement.symbols());

      return new BlockStatement(statement.symbols(), processStatements(statement.statements()));
    } finally {
      symbolTables.pop();
    }
  }

  private List<Statement> processStatements(List<Statement> statements) {
    return statements.stream().map(this::process).toList();
  }

  private List<Operand> processOperands(List<Operand> operands) {
    return operands.stream().map(this::process).toList();
  }

  private List<Parameter> processParameters(List<Parameter> parameters) {
    return parameters.stream().map(this::process).toList();
  }

  private Parameter process(Parameter parameter) {
    return new Parameter(parameter.name(), process(parameter.defaultValue()));
  }

  private SymbolTable getCurrentSymbolTable() {
    return symbolTables.peek();
  }
}
