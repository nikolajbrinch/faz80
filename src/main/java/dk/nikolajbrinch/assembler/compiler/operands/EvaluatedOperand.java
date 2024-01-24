package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.instructions.ValueSupplier;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.assembler.parser.Register;

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
