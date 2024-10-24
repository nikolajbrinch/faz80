package dk.nikolajbrinch.faz80.assembler.operands;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;
import dk.nikolajbrinch.faz80.parser.base.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.base.Condition;
import dk.nikolajbrinch.faz80.parser.base.Register;

public record EvaluatedOperand(
    Object operand, Object displacement, boolean isIndirect, AddressingMode addressingMode) {

  public Register asRegister() {
    return (Register) operand;
  }

  public NumberValue asNumberValue() {
    return (NumberValue) operand;
  }

  public ValueSupplier asValue() {
    return (ValueSupplier) operand;
  }

  public Condition asCondition() {
    return (Condition) operand;
  }

  public long displacementE(Address address) {
    if (operand instanceof ValueSupplier valueSupplier) {
      return OperandUtil.displacementE(valueSupplier, address);
    }

    return OperandUtil.displacementE(asNumberValue(), address);
  }

  public long displacementD() {
    if (displacement instanceof ValueSupplier valueSupplier) {
      return OperandUtil.displacementD(valueSupplier);
    }

    return OperandUtil.displacementD((NumberValue) displacement);
  }
}
