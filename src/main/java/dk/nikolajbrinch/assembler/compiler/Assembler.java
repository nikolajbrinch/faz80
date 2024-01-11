package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.ast.operands.Operand;
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
import dk.nikolajbrinch.assembler.compiler.operands.OperandFactory;
import dk.nikolajbrinch.assembler.compiler.symbols.AddressSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.MacroSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.StatementSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolType;
import dk.nikolajbrinch.assembler.compiler.symbols.UndefinedSymbolException;
import dk.nikolajbrinch.assembler.compiler.symbols.ValueSymbol;
import dk.nikolajbrinch.assembler.compiler.values.BooleanValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.StringValue;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.Logger;
import dk.nikolajbrinch.parser.impl.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Assembler implements StatementVisitor<Void> {

  private final Logger logger = LoggerFactory.getLogger();
  private final InstructionByteSourceFactory instructionByteSourceFactory =
      new InstructionByteSourceFactory();
  private final ExpressionEvaluator expressionEvaluator;
  private final List<ByteSource> bytes = new ArrayList<>();

  private final SymbolTable globals = new SymbolTable();

  private SymbolTable symbols = globals;

  private Address currentAddress = new Address(NumberValue.create(0), NumberValue.create(0));

  private final List<Statement> errors = new ArrayList<>();

  public Assembler(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  public void assemble(List<Statement> statements) {
    for (Statement statement : statements) {
      execute(statement);
    }
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public Void visitExpressionStatement(ExpressionStatement statement) {
    evaluate(statement.expression());

    return null;
  }

  @Override
  public Void visitInstructionStatement(InstructionStatement statement) {
    try {
      Operand operand1 = statement.operand1();
      Operand operand2 = statement.operand2();

      ByteSource byteSource =
          instructionByteSourceFactory.generateByteSource(
              statement.mnemonic(),
              currentAddress,
              new OperandFactory().createOperand(operand1, symbols, expressionEvaluator),
              new OperandFactory().createOperand(operand2, symbols, expressionEvaluator));

      bytes.add(byteSource);

      currentAddress = currentAddress.add(NumberValue.create(byteSource.length()));
    } catch (Exception e) {
      reportStatementError(statement, e);
    }

    return null;
  }

  @Override
  public Void visitConstantStatement(ConstantStatement statement) {
    symbols.define(
        statement.identifier().text(),
        SymbolType.CONSTANT,
        new ValueSymbol(evaluate(statement.value())));

    return null;
  }

  @Override
  public Void visitVariableStatement(VariableStatement statement) {
    symbols.define(
        sanitizeName(statement.identifier().text()),
        SymbolType.VARIABLE,
        new ValueSymbol(evaluate(statement.intializer())));

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
              Value<?> value = evaluate(expression);

              long byteValue;

              if (value instanceof StringValue stringValue) {
                byteValue = stringValue.value().getBytes()[0];
              } else {
                byteValue = ((NumberValue) value).value();
              }

              bytes.add(ByteSource.of(byteValue));
            });

    return null;
  }

  @Override
  public Void visitDataLongStatement(DataLongStatement statement) {
    statement
        .values()
        .forEach(
            expression -> {
              NumberValue value = (NumberValue) evaluate(expression);

              bytes.add(
                  ByteSource.of(
                      value.lsw().lsb().value(),
                      value.lsw().msb().value(),
                      value.msw().lsb().value(),
                      value.msw().msb().value()));
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
              NumberValue value = (NumberValue) evaluate(expression);

              bytes.add(ByteSource.of(value.lsb().value(), value.msb().value()));
            });

    return null;
  }

  @Override
  public Void visitOriginStatement(OriginStatement statement) {
    Object location = evaluate(statement.location());

    if (location instanceof NumberValue value) {
      currentAddress = new Address(value.asWord(), value.asWord());
    } else {
      throw new IllegalStateException("Origin is not a number");
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
        throw new IllegalValueException(address + " is not a valid value for phase address");
      }
      statement.block().accept(this);
    } finally {
      currentAddress =
          new Address(currentAddress.physicalAddress(), currentAddress.physicalAddress());
    }

    return null;
  }

  @Override
  public Void visitRepeatStatement(RepeatStatement statement) {
    throw new IllegalStateException("Macros should have been resolved!");
  }

  @Override
  public Void visitConditionalStatement(ConditionalStatement statement) {
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
      throw new IllegalStateException("Condition is not boolean");
    }

    return null;
  }

  @Override
  public Void visitAssertStatement(AssertStatement statement) {
    Object result = evaluate(statement.expression());

    if (result instanceof BooleanValue value) {
      if (!value.value()) {
        throw new AssertionError("Assertion failed");
      }
    } else {
      throw new IllegalStateException("Condition is not boolean");
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

  private void execute(Statement statement) {
    statement.accept(this);
  }

  private void expandMacro(MacroCallStatement statement) {
    MacroSymbol symbol = null;

    try {
      String macroName = statement.name().text();
      symbol = symbols.get(macroName);
    } catch (UndefinedSymbolException e) {
      reportStatementError(statement, e);
    }

    List<Statement> arguments = statement.arguments();
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
            macroEnvironment.define(name, SymbolType.MACRO_ARGUMENT, new StatementSymbol(argument));
          }
        }
      }
    }

    withSymbols(macroEnvironment, () -> macro.block().accept(this));
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

  private void reportStatementError(Statement statement, Exception e) {
    errors.add(statement);

    switch (e) {
      case AssembleException exception -> {
        Line line = statement.line();
        logger.error("Compilation error in line %d: %s", line.number(), e.getMessage());
        logger.error("    %s%n", line.content().strip());
      }
      default -> throw new IllegalStateException(e);
    }
  }
}
