package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.ast.operands.ConditionOperand;
import dk.nikolajbrinch.assembler.ast.operands.ExpressionOperand;
import dk.nikolajbrinch.assembler.ast.operands.GroupingOperand;
import dk.nikolajbrinch.assembler.ast.operands.RegisterOperand;
import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import dk.nikolajbrinch.assembler.compiler.values.Value;

public class OperandFactory {

  public Operand createOperand(dk.nikolajbrinch.assembler.ast.operands.Operand operand, SymbolTable symbols, ExpressionEvaluator evaluator) {
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

    if (operand instanceof RegisterOperand registerOperand) {
      displacment = registerOperand.displacement();
      evaluatedOperand = registerOperand.register();
    } else if (operand instanceof ConditionOperand conditionOperand) {
      evaluatedOperand = conditionOperand.condition();
    } else if (operand instanceof ExpressionOperand expressionOperand){
      evaluatedOperand = validateOperand(evaluator.evaluate(expressionOperand.expression(), symbols));
    }
    Object evaluatedDisplacement = displacment == null ? null : validateDisplacement(evaluator.evaluate(displacment, symbols));

    AddressingMode addressingMode =
        new AddressingDecoder().decode(evaluatedOperand, isIndirect, displacment != null);

    return new Operand(evaluatedOperand, evaluatedDisplacement, isIndirect, addressingMode);
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
