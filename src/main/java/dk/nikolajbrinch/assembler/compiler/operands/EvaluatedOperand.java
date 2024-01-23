package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.instructions.ValueSupplier;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
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
    NumberValue number;

    if (operand instanceof ValueSupplier valueSupplier) {
      number = valueSupplier.number();
    } else {
      number = asNumberValue();
    }

    NumberValue relative = number.subtract(address.logicalAddress());

    if ((relative.value() - 2) < -126) {
      throw new IllegalDisplacementException(
          operand,
          relative,
          "Displacement e too low: "
              + relative
              + "[current address: "
              + address.logicalAddress().value()
              + ", jump address: "
              + number.value()
              + "]");
    }
    if ((relative.value() - 2) > 129) {
      throw new IllegalDisplacementException(
          operand,
          relative,
          "Displacement e too high: "
              + relative
              + "[current address: "
              + address.logicalAddress().value()
              + ", jump address: "
              + number.value()
              + "]");
    }

    long twosComplement = NumberValue.twosComplement(relative.value() - 2) & 0XFF;

    return new NumberValue(twosComplement, Size.BYTE).value() & 0xFF;
  }

  public long displacementD() {
    NumberValue number;

    if (displacement instanceof ValueSupplier valueSupplier) {
      number = valueSupplier.number();
    } else {
      number = (NumberValue) displacement;
    }

    return (NumberValue.twosComplement(number.value())) & 0xFF;
  }
}
