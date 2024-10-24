package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.assembler.instructions.IllegalInstructionException;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.OperandEvaluator;
import dk.nikolajbrinch.faz80.parser.Parameter;
import dk.nikolajbrinch.faz80.parser.base.values.BooleanValue;
import dk.nikolajbrinch.faz80.parser.base.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.base.values.StringValue;
import dk.nikolajbrinch.faz80.parser.base.values.Value;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.evaluator.Context;
import dk.nikolajbrinch.faz80.parser.evaluator.EvaluationException;
import dk.nikolajbrinch.faz80.parser.evaluator.ExpressionEvaluator;
import dk.nikolajbrinch.faz80.parser.expressions.Expression;
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
import dk.nikolajbrinch.faz80.parser.statements.ValuesStatement;
import dk.nikolajbrinch.faz80.parser.symbols.Macro;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolException;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolType;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Assembler implements StatementProcessor<Void> {

  private final InstructionByteSourceFactory instructionByteSourceFactory =
      new InstructionByteSourceFactory();

  private final OperandEvaluator operandEvaluator = new OperandEvaluator();
  private final ExpressionEvaluator expressionEvaluator;
  private final Assembled assembled = new Assembled();
  private SymbolTable globals;
  private SymbolTable symbols = globals;
  private final List<AssembleMessage> errors = new ArrayList<>();
  private Address currentAddress =
      new Address(NumberValue.createWord(0), NumberValue.createWord(0));

  public Assembler(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  public AssembleResult assemble(BlockStatement block) {
    globals = new SymbolTable();
    symbols = block.symbols().copy(globals);

    process(block);

    return new AssembleResult(assembled, errors);
  }

  @Override
  public Void processSectionStatement(SectionStatement statement) {
    return null;
  }

  @Override
  public Void processExpressionStatement(ExpressionStatement statement) {
    try {
      evaluate(statement.expression());
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void processInstructionStatement(InstructionStatement statement) {
    try {
      List<EvaluatedOperand> operands =
          statement.operands().stream()
              .map(
                  operand ->
                      operandEvaluator.evaluate(
                          operand, new Context(symbols, currentAddress), expressionEvaluator))
              .toList();

      ByteSource byteSource =
          instructionByteSourceFactory.generateByteSource(
              statement.mnemonic(), currentAddress, operands);

      assembled.add(statement, byteSource);

      if (byteSource == null) {
        reportError(
            new AssembleException(
                statement,
                "No instruction generated for instruction: " + statement.mnemonic().text()));
      } else {
        currentAddress = currentAddress.add(NumberValue.create(byteSource.length()));
      }
    } catch (IllegalInstructionException | EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void processAssignStatement(AssignStatement statement) {
    try {
      symbols.assign(
          sanitizeName(statement.identifier().text()),
          statement.type(),
          Optional.of(evaluate(statement.initializer())));
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void processCommentStatement(CommentStatement statement) {
    return null;
  }

  @Override
  public Void processEndStatement(EndStatement endStatement) {
    return null;
  }

  private String sanitizeName(String text) {
    while (text.startsWith(".")) {
      text = text.substring(1);
    }

    while (text.endsWith(":")) {
      text = text.substring(0, text.length() - 1);
    }

    return text;
  }

  @Override
  public Void processDataByteStatement(DataByteStatement statement) {
    handleValuesStatement(statement, Assembler::byteToByteSuppliers);

    return null;
  }

  @Override
  public Void processDataLongStatement(DataLongStatement statement) {
    handleValuesStatement(statement, Assembler::longToByteSuppliers);

    return null;
  }

  @Override
  public Void processDataTextStatement(DataTextStatement statement) {
    handleValuesStatement(statement, Assembler::textToByteSuppliers);

    return null;
  }

  @Override
  public Void processDataWordStatement(DataWordStatement statement) {
    handleValuesStatement(statement, Assembler::wordToByteSuppliers);

    return null;
  }

  @Override
  public Void processOriginStatement(OriginStatement statement) {
    try {
      Object location = evaluate(statement.location());

      if (location instanceof NumberValue value) {
        currentAddress = new Address(value.asWord(), value.asWord());
        assembled.setOrigin(currentAddress.physicalAddress());
      } else {
        throw new IllegalStateException("Origin is not a number");
      }
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void processAlignStatement(AlignStatement statement) {
    return null;
  }

  @Override
  public Void processBlockStatement(BlockStatement statement) {
    withSymbols(
        statement.symbols().copy(),
        () ->
            statement.statements().stream()
                .filter(s -> !(s instanceof EmptyStatement))
                .filter(s -> !(s instanceof CommentStatement))
                .forEach(this::execute));

    return null;
  }

  @Override
  public Void processLocalStatement(LocalStatement statement) {
    process(statement.block());

    return null;
  }

  @Override
  public Void processMacroStatement(MacroStatement statement) {
    return null;
  }

  @Override
  public Void processPhaseStatement(PhaseStatement statement) {
    try {
      Object address = evaluate(statement.expression());
      if (address instanceof NumberValue numberValue) {
        currentAddress = new Address(numberValue, currentAddress.physicalAddress());
      } else {
        throw new IllegalValueException(
            statement, address + " is not a valid value for phase address");
      }
      process(statement.block());
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    } finally {
      currentAddress =
          new Address(currentAddress.physicalAddress(), currentAddress.physicalAddress());
    }

    return null;
  }

  @Override
  public Void processRepeatStatement(RepeatStatement statement) {
    try {
      Object result = evaluate(statement.count());

      if (result instanceof NumberValue value) {
        long count = value.value();

        if (count < 0) {
          reportError(new AssembleException(statement, "Count is negative"));
        }

        for (int i = 0; i < count; i++) {
          process(statement.block());
        }
      } else {
        reportError(new AssembleException(statement, "Count is not a number"));
      }

    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void processConditionalStatement(ConditionalStatement statement) {
    try {
      Object result = evaluate(statement.condition());

      if (result instanceof BooleanValue value) {
        if (value.value()) {
          process(statement.thenBranch());
        } else {
          if (statement.elseBranch() != null) {
            process(statement.elseBranch());
          }
        }
      } else {
        reportError(new AssembleException(statement, "Condition is not boolean"));
      }
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void processAssertStatement(AssertStatement statement) {
    try {
      Object result = evaluate(statement.expression());

      if (result instanceof BooleanValue value) {
        if (!value.value()) {
          throw new AssertionError("Assertion failed");
        }
      } else {
        throw new IllegalStateException("Condition is not boolean");
      }
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void processGlobalStatement(GlobalStatement statement) {
    return null;
  }

  @Override
  public Void processMacroCallStatement(MacroCallStatement statement) {
    expandMacro(statement);

    return null;
  }

  @Override
  public Void processEmptyStatement(EmptyStatement statement) {
    return null;
  }

  @Override
  public Void processInsertStatement(InsertStatement statement) {
    return null;
  }

  @Override
  public Void processIncludeStatement(IncludeStatement statement) {
    return null;
  }

  private void execute(Statement statement) {
    process(statement);
  }

  private void expandMacro(MacroCallStatement statement) {
    String macroName = statement.name().text();
    List<Statement> arguments = statement.arguments();

    Optional optional = null;

    try {
      optional = symbols.get(macroName);

      Macro macro = optional.isPresent() ? (Macro) optional.get() : null;

      SymbolTable macroEnvironment = new SymbolTable(symbols);

      for (int i = 0; i < Math.max(arguments.size(), macro.parameters().size()); i++) {
        Statement argument = i < arguments.size() ? arguments.get(i) : null;
        Parameter parameter = i < macro.parameters().size() ? macro.parameters().get(i) : null;

        if (parameter != null) {
          String name = parameter.name().text();

          Value<?> value;

          Expression defaultValue = parameter.defaultValue();

          macroEnvironment.define(name, SymbolType.MACRO_ARGUMENT);
          macroEnvironment.assign(name, SymbolType.MACRO_ARGUMENT, Optional.empty());

          if (defaultValue != null) {
            macroEnvironment.assign(
                name, SymbolType.MACRO_ARGUMENT, Optional.of(evaluate(defaultValue)));
          }

          if (!(defaultValue != null && argument == null)) {
            if (argument instanceof ExpressionStatement expressionStatement) {
              macroEnvironment.assign(
                  name,
                  SymbolType.MACRO_ARGUMENT,
                  Optional.of(evaluate(expressionStatement.expression())));
            }
          }
        }
      }

      withSymbols(macroEnvironment, () -> process(macro.block()));
    } catch (SymbolException | EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }
  }

  private Value<?> evaluate(Expression expression) {
    return expressionEvaluator.evaluate(expression, new Context(symbols, currentAddress)).val();
  }

  private void withSymbols(SymbolTable symbolTable, Runnable runnable) {
    SymbolTable previous = this.symbols;

    try {
      this.symbols = symbolTable;
      runnable.run();
    } finally {
      this.symbols = previous;
    }
  }

  private void reportError(AssembleException e) {
    errors.add(AssembleMessage.error(e));
  }

  private void handleValuesStatement(
      ValuesStatement statement, Function<Value<?>, Stream<ByteSupplier>> valueFunction) {
    List<ByteSupplier> byteSuppliers = null;

    try {
      byteSuppliers =
          statement.values().stream()
              .flatMap(expression -> valueFunction.apply(evaluate(expression)))
              .toList();
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    if (byteSuppliers != null) {
      assembled.add(statement, ByteSource.of(byteSuppliers));
      currentAddress = currentAddress.add(NumberValue.create(byteSuppliers.size()));
    }
  }

  private static Stream<ByteSupplier> byteToByteSuppliers(Value<?> value) {
    if (value instanceof StringValue stringValue) {
      if (stringValue.canBeNumber()) {
        return Stream.of(ByteSupplier.of(stringValue.asNumberValue().value()));
      }

      ByteBuffer buffer = ByteBuffer.wrap(stringValue.value().getBytes());
      return Stream.generate(buffer::get).limit(buffer.capacity()).map(ByteSupplier::of);
    }

    return Stream.of(ByteSupplier.of(((NumberValue) value).value()));
  }

  private static Stream<ByteSupplier> wordToByteSuppliers(Value<?> value) {
    NumberValue numberValue = (NumberValue) value;

    return Stream.of(
        ByteSupplier.of(numberValue.lsb().value()), ByteSupplier.of(numberValue.msb().value()));
  }

  private static Stream<ByteSupplier> longToByteSuppliers(Value<?> value) {
    NumberValue numberValue = (NumberValue) value;

    return Stream.of(
        ByteSupplier.of(numberValue.lsw().lsb().value()),
        ByteSupplier.of(numberValue.lsw().msb().value()),
        ByteSupplier.of(numberValue.msw().lsb().value()),
        ByteSupplier.of(numberValue.msw().msb().value()));
  }

  private static Stream<ByteSupplier> textToByteSuppliers(Value<?> value) {
    StringValue stringValue = (StringValue) value;

    ByteBuffer buffer = ByteBuffer.wrap(stringValue.value().getBytes());
    return Stream.generate(buffer::get).limit(buffer.capacity()).map(ByteSupplier::of);
  }
}
