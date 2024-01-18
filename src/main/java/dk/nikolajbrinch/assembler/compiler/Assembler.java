package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.instructions.InstructionException;
import dk.nikolajbrinch.assembler.compiler.operands.OperandFactory;
import dk.nikolajbrinch.assembler.compiler.symbols.AddressSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.MacroSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.StatementSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolException;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolType;
import dk.nikolajbrinch.assembler.compiler.symbols.ValueSymbol;
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
import dk.nikolajbrinch.assembler.parser.statements.VariableStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Assembler implements StatementVisitor<Void> {

  private final InstructionByteSourceFactory instructionByteSourceFactory =
      new InstructionByteSourceFactory();
  private final ExpressionEvaluator expressionEvaluator;
  private final List<ByteSource> bytes = new ArrayList<>();

  private final SymbolTable globals = new SymbolTable();
  private final List<AssembleError> errors = new ArrayList<>();
  private SymbolTable symbols = globals;
  private Address currentAddress = new Address(NumberValue.create(0), NumberValue.create(0));

  public Assembler(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  public List<AssembleError> getErrors() {
    return errors;
  }

  public void assemble(BlockStatement block) {
    for (Statement statement : block.statements()) {
      execute(statement);
    }
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public List<ByteSource> getBytes() {
    return bytes;
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
      ByteSource byteSource =
          instructionByteSourceFactory.generateByteSource(
              statement.mnemonic(),
              currentAddress,
              statement.operands().stream()
                  .map(
                      operand ->
                          new OperandFactory().createOperand(operand, symbols, expressionEvaluator))
                  .toList());

      bytes.add(byteSource);

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
      symbols.define(
          statement.identifier().text(),
          SymbolType.CONSTANT,
          new ValueSymbol(evaluate(statement.value())));
    } catch (EvaluationException e) {
      reportError(new AssembleException(statement, e.getMessage(), e));
    }

    return null;
  }

  @Override
  public Void visitVariableStatement(VariableStatement statement) {
    try {
      symbols.define(
          sanitizeName(statement.identifier().text()),
          SymbolType.VARIABLE,
          new ValueSymbol(evaluate(statement.intializer())));
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

    statement
        .values()
        .forEach(
            expression -> {
              try {
                Value<?> value = evaluate(expression);

                long byteValue;

                if (value instanceof StringValue stringValue) {
                  byteValue = stringValue.value().getBytes()[0];
                } else {
                  byteValue = ((NumberValue) value).value();
                }

                bytes.add(ByteSource.of(byteValue));
              } catch (EvaluationException e) {
                reportError(new AssembleException(statement, e.getMessage(), e));
              }
            });
    return null;
  }

  @Override
  public Void visitDataLongStatement(DataLongStatement statement) {
    statement
        .values()
        .forEach(
            expression -> {
              try {
                NumberValue value = (NumberValue) evaluate(expression);

                bytes.add(
                    ByteSource.of(
                        value.lsw().lsb().value(),
                        value.lsw().msb().value(),
                        value.msw().lsb().value(),
                        value.msw().msb().value()));
              } catch (EvaluationException e) {
                reportError(new AssembleException(statement, e.getMessage(), e));
              }
            });

    return null;
  }

  @Override
  public Void visitDataTextStatement(DataTextStatement statement) {
    return null;
  }

  @Override
  public Void visitDataWordStatement(DataWordStatement statement) {
    statement
        .values()
        .forEach(
            expression -> {
              try {
                NumberValue value = (NumberValue) evaluate(expression);

                bytes.add(ByteSource.of(value.lsb().value(), value.msb().value()));
              } catch (EvaluationException e) {
                reportError(new AssembleException(statement, e.getMessage(), e));
              }
            });

    return null;
  }

  @Override
  public Void visitOriginStatement(OriginStatement statement) {
    try {
      Object location = evaluate(statement.location());

      if (location instanceof NumberValue value) {
        currentAddress = new Address(value.asWord(), value.asWord());
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
    statement.statements().forEach(this::execute);

    return null;
  }

  @Override
  public Void visitLocalStatement(LocalStatement statement) {
    withSymbols(new SymbolTable(symbols), () -> statement.block().accept(this));

    return null;
  }

  @Override
  public Void visitMacroStatement(MacroStatement statement) {
    String name = statement.name().text();
    List<Parameter> parameters = statement.parameters();
    Statement block = statement.block();

    symbols.define(name, SymbolType.MACRO, new MacroSymbol(new Macro(name, parameters, block)));

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
        for (int i = 0; i < value.value(); i++) {
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
    globals.define(statement.identifier().text(), SymbolType.UNKNOWN, null);

    return null;
  }

  @Override
  public Void visitLabelStatement(LabelStatement statement) {
    globals.define(
        statement.identifier().text(), SymbolType.LABEL, new AddressSymbol(currentAddress));

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

    MacroSymbol symbol = null;

    try {
      symbol = symbols.get(macroName);

      Macro macro = symbol.value();

      SymbolTable macroEnvironment = new SymbolTable(symbols);

      for (int i = 0; i < Math.max(arguments.size(), macro.parameters().size()); i++) {
        Statement argument = i < arguments.size() ? arguments.get(i) : null;
        Parameter parameter = i < macro.parameters().size() ? macro.parameters().get(i) : null;

        if (parameter != null) {
          String name = parameter.name().text();

          Value<?> value;

          Expression defaultValue = parameter.defaultValue();

          if (defaultValue != null) {
            macroEnvironment.define(
                name, SymbolType.MACRO_ARGUMENT, new ValueSymbol(evaluate(defaultValue)));
          }

          if (!(defaultValue != null && argument == null)) {
            if (argument instanceof ExpressionStatement expressionStatement) {
              macroEnvironment.define(
                  name,
                  SymbolType.MACRO_ARGUMENT,
                  new ValueSymbol(evaluate(expressionStatement.expression())));
            } else {
              macroEnvironment.define(
                  name, SymbolType.MACRO_ARGUMENT, new StatementSymbol(argument));
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
    return expressionEvaluator.evaluate(expression, symbols, currentAddress);
  }

  private void withSymbols(SymbolTable symbolTable, Supplier<Void> supplier) {
    SymbolTable previous = this.symbols;

    try {
      this.symbols = symbolTable;
      supplier.get();
    } finally {
      this.symbols = previous;
    }
  }

  private void reportError(AssembleException e) {
    errors.add(new AssembleError(e));
  }
}
