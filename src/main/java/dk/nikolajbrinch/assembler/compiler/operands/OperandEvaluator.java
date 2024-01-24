package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.Evaluated;
import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.compiler.instructions.ValueSupplier;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.operands.ConditionOperand;
import dk.nikolajbrinch.assembler.parser.operands.ExpressionOperand;
import dk.nikolajbrinch.assembler.parser.operands.GroupingOperand;
import dk.nikolajbrinch.assembler.parser.operands.Operand;
import dk.nikolajbrinch.assembler.parser.operands.RegisterOperand;
import dk.nikolajbrinch.parser.Logger;
import dk.nikolajbrinch.parser.impl.LoggerFactory;

public class OperandEvaluator {

  Logger logger = LoggerFactory.getLogger();

  AddressingModeDecoder addressingModeDecoder = new AddressingModeDecoder();

  public EvaluatedOperand evaluate(
      Operand operand, Context context, ExpressionEvaluator expressionEvaluator) {
    if (operand == null) {
      return null;
    }

    boolean isIndirect = false;

    if (operand instanceof GroupingOperand grouping) {
      isIndirect = true;
      operand = grouping.operand();
    }

    Expression displacment = null;

    Object evaluatedOperand = null;
    Object evaluatedDisplacement = null;

    AddressingMode addressingMode = null;

    if (operand instanceof RegisterOperand registerOperand) {
      displacment = registerOperand.displacement();
      evaluatedOperand = registerOperand.register();
      addressingMode =
          addressingModeDecoder.decodeRegister(
              registerOperand.register(), isIndirect, displacment != null);
    } else if (operand instanceof ConditionOperand conditionOperand) {
      evaluatedOperand = conditionOperand.condition();
    } else if (operand instanceof ExpressionOperand expressionOperand) {
      Evaluated evaluated = expressionEvaluator.evaluate(expressionOperand.expression(), context);

      if (evaluated.size() == Size.UNKNOWN) {
        logger.warn("Size is unknown for operand: " + operand);
      }

      addressingMode = addressingModeDecoder.decodeExpression(evaluated.size(), isIndirect);
      evaluatedOperand =
          evaluated.isLazy() ? evaluated.valueSupplier() : ValueSupplier.of(evaluated::val);
    }

    if (displacment != null) {
      Evaluated evaluated = expressionEvaluator.evaluate(displacment, context);
      evaluatedDisplacement =
          evaluated.isLazy() ? evaluated.valueSupplier() : ValueSupplier.of(evaluated::val);
    }

    return new EvaluatedOperand(
        evaluatedOperand, evaluatedDisplacement, isIndirect, addressingMode);
  }

  private Object validateOperand(Value<?> operand) {
    if (operand instanceof NumberValue n && (n.size() == Size.BYTE || n.size() == Size.WORD)) {
      return operand;
    }

    if (operand instanceof Value v) {
      return v.asNumberValue();
    }

    throw new IllegalStateException("Invalid operand");
  }

  private Object validateDisplacement(Object displacement) {
    if (displacement instanceof NumberValue n && n.size() == Size.BYTE) {
      return displacement;
    }

    throw new IllegalStateException("Invalid displacement");
  }
}
