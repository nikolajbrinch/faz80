package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.instructions.InstructionException;
import dk.nikolajbrinch.assembler.compiler.operands.Context;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;
import dk.nikolajbrinch.assembler.compiler.operands.OperandEvaluator;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolException;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolType;
import dk.nikolajbrinch.assembler.compiler.values.BooleanValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.StringValue;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.statements.AlignStatement;
import dk.nikolajbrinch.assembler.parser.statements.AssertStatement;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.parser.statements.DataByteStatement;
import dk.nikolajbrinch.assembler.parser.statements.DataLongStatement;
import dk.nikolajbrinch.assembler.parser.statements.DataTextStatement;
import dk.nikolajbrinch.assembler.parser.statements.DataWordStatement;
import dk.nikolajbrinch.assembler.parser.statements.EmptyStatement;
import dk.nikolajbrinch.assembler.parser.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.parser.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.parser.statements.IncludeStatement;
import dk.nikolajbrinch.assembler.parser.statements.InsertStatement;
import dk.nikolajbrinch.assembler.parser.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.parser.statements.LabelStatement;
import dk.nikolajbrinch.assembler.parser.statements.LocalStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroStatement;
import dk.nikolajbrinch.assembler.parser.statements.OriginStatement;
import dk.nikolajbrinch.assembler.parser.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.parser.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.parser.statements.StatementVisitor;
import dk.nikolajbrinch.assembler.parser.statements.ValuesStatement;
import dk.nikolajbrinch.assembler.parser.statements.VariableStatement;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Assembler implements StatementVisitor<Void> {

  private final InstructionByteSourceFactory instructionByteSourceFactory =
      new InstructionByteSourceFactory();

  private final OperandEvaluator operandEvaluator = new OperandEvaluator();
  private final ExpressionEvaluator expressionEvaluator;
  private final Assembled assembled = new Assembled();
  private final SymbolTable globals = new SymbolTable();
  private final List<AssembleError> errors = new ArrayList<>();
  private SymbolTable symbols = globals;
  private Address currentAddress =
      new Address(NumberValue.createWord(0), NumberValue.createWord(0));

  public Assembler(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  public List<AssembleError> getErrors() {
    return errors;
  }

  public Assembled assemble(BlockStatement block) {
    block.accept(this);

    return assembled;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public Void visitExpressionStatement(ExpressionStatement statement) {
    try {
      evaluate(statement.expression());
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void visitInstructionStatement(InstructionStatement statement) {
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

      assembled.add(byteSource);

      if (byteSource == null) {
        reportError(
            new AssembleException(
                statement,
                "No instruction generated for instruction: " + statement.mnemonic().text()));
      } else {
        currentAddress = currentAddress.add(NumberValue.create(byteSource.length()));
      }
    } catch (InstructionException | EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void visitConstantStatement(ConstantStatement statement) {
    try {
      symbols.assign(
          statement.identifier().text(),
          SymbolType.CONSTANT,
          Optional.ofNullable(evaluate(statement.value())));
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void visitVariableStatement(VariableStatement statement) {
    try {
      symbols.assign(
          sanitizeName(statement.identifier().text()),
          SymbolType.VARIABLE,
          Optional.of(evaluate(statement.intializer())));
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

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
  public Void visitDataByteStatement(DataByteStatement statement) {
    handleValuesStatement(statement, Assembler::byteToByteSuppliers);

    return null;
  }

  @Override
  public Void visitDataLongStatement(DataLongStatement statement) {
    handleValuesStatement(statement, Assembler::longToByteSuppliers);

    return null;
  }

  @Override
  public Void visitDataTextStatement(DataTextStatement statement) {
    handleValuesStatement(statement, Assembler::textToByteSuppliers);

    return null;
  }

  @Override
  public Void visitDataWordStatement(DataWordStatement statement) {
    handleValuesStatement(statement, Assembler::wordToByteSuppliers);

    return null;
  }

  @Override
  public Void visitOriginStatement(OriginStatement statement) {
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
  public Void visitAlignStatement(AlignStatement statement) {
    return null;
  }

  @Override
  public Void visitBlockStatement(BlockStatement statement) {
    withSymbols(statement.symbolTable(), () -> statement.statements().forEach(this::execute));

    return null;
  }

  @Override
  public Void visitLocalStatement(LocalStatement statement) {
    statement.block().accept(this);

    return null;
  }

  @Override
  public Void visitMacroStatement(MacroStatement statement) {
    return null;
  }

  @Override
  public Void visitPhaseStatement(PhaseStatement statement) {
    try {
      Object address = evaluate(statement.expression());
      if (address instanceof NumberValue numberValue) {
        currentAddress = new Address(numberValue, currentAddress.physicalAddress());
      } else {
        throw new IllegalValueException(
            statement, address + " is not a valid value for phase address");
      }
      statement.block().accept(this);
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    } finally {
      currentAddress =
          new Address(currentAddress.physicalAddress(), currentAddress.physicalAddress());
    }

    return null;
  }

  @Override
  public Void visitRepeatStatement(RepeatStatement statement) {
    try {
      Object result = evaluate(statement.count());

      if (result instanceof NumberValue value) {
        long count = value.value();

        if (count < 0) {
          reportError(new AssembleException(statement, "Count is negative"));
        }

        for (int i = 0; i < count; i++) {
          statement.blockStatement().accept(this);
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
  public Void visitConditionalStatement(ConditionalStatement statement) {
    try {
      Object result = evaluate(statement.condition());

      if (result instanceof BooleanValue value) {
        if (value.value()) {
          statement.thenBranch().accept(this);
        } else {
          if (statement.elseBranch() != null) {
            statement.elseBranch().accept(this);
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
  public Void visitAssertStatement(AssertStatement statement) {
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
  public Void visitGlobalStatement(GlobalStatement statement) {
    return null;
  }

  @Override
  public Void visitLabelStatement(LabelStatement statement) {
    symbols.assign(
        statement.identifier().text(),
        SymbolType.LABEL,
        Optional.ofNullable(currentAddress.logicalAddress()));

    return null;
  }

  @Override
  public Void visitMacroCallStatement(MacroCallStatement statement) {
    expandMacro(statement);

    return null;
  }

  @Override
  public Void visitEmptyStatement(EmptyStatement statement) {
    return null;
  }

  @Override
  public Void visitInsertStatement(InsertStatement statement) {
    return null;
  }

  @Override
  public Void visitIncludeStatement(IncludeStatement includeStatement) {
    return null;
  }

  private void execute(Statement statement) {
    statement.accept(this);
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

      withSymbols(macroEnvironment, () -> macro.block().accept(this));
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
    errors.add(new AssembleError(e));
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
      assembled.add(ByteSource.of(byteSuppliers));
      currentAddress = currentAddress.add(NumberValue.create(byteSuppliers.size()));
    }
  }

  private static Stream<ByteSupplier> byteToByteSuppliers(Value<?> value) {
    if (value instanceof StringValue stringValue) {
      if (stringValue.canBeNUmber()) {
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
