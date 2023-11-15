package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.operands.AddressingDecoder;
import dk.nikolajbrinch.assembler.compiler.operands.OperandFactory;
import dk.nikolajbrinch.assembler.compiler.values.BooleanValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.assembler.parser.Environment;
import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.statements.AlignStatement;
import dk.nikolajbrinch.assembler.parser.statements.AssertStatement;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.assembler.parser.statements.ByteStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.parser.statements.EmptyStatement;
import dk.nikolajbrinch.assembler.parser.statements.EndStatement;
import dk.nikolajbrinch.assembler.parser.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.parser.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.parser.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.parser.statements.LabelStatement;
import dk.nikolajbrinch.assembler.parser.statements.LocalStatement;
import dk.nikolajbrinch.assembler.parser.statements.LongStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroStatement;
import dk.nikolajbrinch.assembler.parser.statements.OriginStatement;
import dk.nikolajbrinch.assembler.parser.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.parser.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.parser.statements.StatementVisitor;
import dk.nikolajbrinch.assembler.parser.statements.VariableStatement;
import dk.nikolajbrinch.assembler.parser.statements.WordStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Assembler implements StatementVisitor<Void> {

  private final InstructionByteSourceFactory instructionByteSourceFactory =
      new InstructionByteSourceFactory();
  private final AddressingDecoder addressingModeDecoder = new AddressingDecoder();
  private final ExpressionEvaluator expressionEvaluator;
  private final List<ByteSource> bytes = new ArrayList<>();

  private final Environment globals = new Environment();

  private Environment environment = globals;

  private NumberValue currentAddress = NumberValue.create(0);

  public Assembler(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  public void assemble(List<Statement> statements) {
    try {
      for (Statement statement : statements) {
        execute(statement);
      }
    } catch (EndException e) {
      /*
       * End of code reached
       */
    }
  }

  @Override
  public Void visitExpressionStatement(ExpressionStatement statement) {
    evaluate(statement.expression());

    return null;
  }

  @Override
  public Void visitInstructionStatement(InstructionStatement statement) {
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

    return null;
  }

  private Object validateOperand(Object operand) {
    if (operand instanceof Register) {
      return operand;
    }

    if (operand instanceof Condition) {
      return operand;
    }

    if (operand instanceof NumberValue n && (n.size() == Size.BYTE || n.size() == Size.WORD)) {
      return operand;
    }

    if (operand instanceof Value v) {
      return v.asNumberValue();
    }

    throw new IllegalStateException("Invalid operand");
  }

  @Override
  public Void visitConstantStatement(ConstantStatement statement) {
    environment.define(statement.identifier().text(), evaluate(statement.value()));

    return null;
  }

  @Override
  public Void visitVariableStatement(VariableStatement statement) {
    environment.define(statement.identifier().text(), evaluate(statement.intializer()));

    return null;
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
      currentAddress = value.asWord();
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
    statement.block().accept(this);

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
  public Void visitEndStatement(EndStatement statement) {
    throw new EndException(statement.end().text());
  }

  @Override
  public Void visitEmptyStatement(EmptyStatement emptyStatement) {
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
