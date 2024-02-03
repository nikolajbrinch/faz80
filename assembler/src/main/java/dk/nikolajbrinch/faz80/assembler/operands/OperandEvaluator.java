package dk.nikolajbrinch.faz80.assembler.operands;

import dk.nikolajbrinch.faz80.parser.evaluator.Context;
import dk.nikolajbrinch.faz80.parser.evaluator.Evaluated;
import dk.nikolajbrinch.faz80.parser.evaluator.ExpressionEvaluator;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;
import dk.nikolajbrinch.faz80.parser.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.values.NumberValue.Size;
import dk.nikolajbrinch.faz80.parser.values.Value;
import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.parser.operands.ConditionOperand;
import dk.nikolajbrinch.faz80.parser.operands.ExpressionOperand;
import dk.nikolajbrinch.faz80.parser.operands.GroupingOperand;
import dk.nikolajbrinch.faz80.parser.operands.Operand;
import dk.nikolajbrinch.faz80.parser.operands.RegisterOperand;
import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;

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
