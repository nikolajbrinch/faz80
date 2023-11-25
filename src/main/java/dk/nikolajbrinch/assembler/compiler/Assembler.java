package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
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
import dk.nikolajbrinch.assembler.compiler.operands.OperandFactory;
import dk.nikolajbrinch.assembler.compiler.values.BooleanValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Environment;
import dk.nikolajbrinch.assembler.util.LineConstructor;
import dk.nikolajbrinch.assembler.util.LineConstructor.ConstructedLine;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Assembler implements StatementVisitor<Void> {

  private final InstructionByteSourceFactory instructionByteSourceFactory =
      new InstructionByteSourceFactory();
  private final ExpressionEvaluator expressionEvaluator;
  private final List<ByteSource> bytes = new ArrayList<>();

  private final Environment globals = new Environment();

  private Environment environment = globals;

  private Address currentAddress = new Address(NumberValue.create(0), NumberValue.create(0));

  private List<ConstructedLine> errors = new ArrayList<>();

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

  private void reportStatementError(Statement statement, Exception e) {
    ConstructedLine line = new LineConstructor().constructLine(statement);

    errors.add(line);

    switch (e) {
      case AssembleException exception -> {
        System.out.println(String.format("Error in line: %d", line.number()));
        System.out.println(line.content());
        System.out.println(e.getMessage());
      }
      default -> throw new IllegalStateException(e);
    }
  }

  @Override
  public Void visitExpressionStatement(ExpressionStatement statement) {
    evaluate(statement.expression());

    return null;
  }

  @Override
  public Void visitInstructionStatement(InstructionStatement statement) {
    try {
      Expression operand1 = statement.operand1();
      Expression operand2 = statement.operand2();

      ByteSource byteSource =
          instructionByteSourceFactory.generateByteSource(
              statement.mnemonic(),
              currentAddress,
              new OperandFactory().createOperand(operand1, this::evaluate),
              new OperandFactory().createOperand(operand2, this::evaluate));

      bytes.add(byteSource);

      currentAddress = currentAddress.add(NumberValue.create(byteSource.length()));
    } catch (Exception e) {
      reportStatementError(statement, e);
    }

    return null;
  }

  @Override
  public Void visitConstantStatement(ConstantStatement statement) {
    environment.define(statement.identifier().text(), evaluate(statement.value()));

    return null;
  }

  @Override
  public Void visitVariableStatement(VariableStatement statement) {
    environment.define(
        sanitizeName(statement.identifier().text()), evaluate(statement.intializer()));

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
  public Void visitByteStatement(ByteStatement statement) {
    statement
        .values()
        .forEach(
            expression -> {
              NumberValue value = (NumberValue) evaluate(expression);

              bytes.add(ByteSource.of(value.value()));
            });

    return null;
  }

  @Override
  public Void visitLongStatement(LongStatement statement) {
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
  public Void visitWordStatement(WordStatement statement) {
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
    withEnvironment(new Environment(environment), () -> statement.block().accept(this));

    return null;
  }

  @Override
  public Void visitMacroStatement(MacroStatement statement) {
    throw new IllegalStateException("Macros should have been resolved!");
  }

  @Override
  public Void visitPhaseStatement(PhaseStatement statement) {
    try {
      Object address = evaluate(statement.expression());
      if (address instanceof NumberValue numberValue) {
        currentAddress = new Address(numberValue, currentAddress.physicalAddress());
      } else {
        throw new IllegalValueException(
            String.valueOf(address) + " is not a valid value for phase address");
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
    globals.define(statement.identifier().text(), null);

    return null;
  }

  @Override
  public Void visitLabelStatement(LabelStatement statement) {
    globals.define(statement.identifier().text(), currentAddress);

    return null;
  }

  @Override
  public Void visitMacroCallStatement(MacroCallStatement statement) {
    throw new IllegalStateException("Macro calls should have been resolved!");
  }

  @Override
  public Void visitEmptyStatement(EmptyStatement statement) {
    return null;
  }

  @Override
  public Void visitIncludeStatement(IncludeStatement statement) {
    return null;
  }

  @Override
  public Void visitInsertStatement(InsertStatement statement) {
    return null;
  }

  private void execute(Statement statement) {
    statement.accept(this);
  }

  private Object evaluate(Expression expression) {
    return expressionEvaluator.evaluate(expression, environment, currentAddress);
  }

  private void withEnvironment(Environment environment, Supplier<Void> supplier) {
    Environment previous = this.environment;

    try {
      this.environment = environment;
      supplier.get();
    } finally {
      this.environment = previous;
    }
  }
}
