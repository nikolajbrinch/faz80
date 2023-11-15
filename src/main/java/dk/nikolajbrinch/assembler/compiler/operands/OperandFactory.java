package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.ast.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.ast.expressions.RegisterExpression;
import java.util.function.Function;

public class OperandFactory {

  public Operand createOperand(Expression operand, Function<Expression, Object> evaluator) {
    if (operand == null) {
      return null;
    }

    boolean isIndirect = false;

    if (operand instanceof GroupingExpression expression) {
      isIndirect = true;
      operand = expression.expression();
    }

    Expression displacment = null;

    if (operand instanceof RegisterExpression registerExpression) {
      displacment = registerExpression.displacement();
    }

    Object evaluatedOperand = validateOperand(evaluator.apply(operand));
    Object evaluatedDisplacement = validateDisplacement(evaluator.apply(displacment));

    AddressingMode addressingMode =
        new AddressingDecoder().decode(evaluatedOperand, isIndirect, displacment != null);

    return new Operand(evaluatedOperand, evaluatedDisplacement, isIndirect, addressingMode);
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

  private Object validateDisplacement(Object displacement) {
    if (displacement instanceof NumberValue n && n.size() == Size.BYTE) {
      return displacement;
    }

    throw new IllegalStateException("Invalid displacement");
  }
}
