package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.assembler.parser.Register;

public record Operand(
    Object operand, Object displacement, boolean isIndirect, AddressingMode addressingMode) {

  public Register asRegister() {
    return (Register) operand;
  }

  public NumberValue asNumberValue() {
    return (NumberValue) operand;
  }

  public Condition asCondition() {
    return (Condition) operand;
  }

  public NumberValue displacementE(Address address) {
    NumberValue relative = asNumberValue().subtract(address.logicalAddress());

    if (relative.value() < -126) {
      throw new IllegalStateException("Displacement e too low: " + relative);
    }
    if (relative.value() > 129) {
      throw new IllegalStateException("Displacement e too high: " + relative);
    }

    long twosComplement = NumberValue.twosComplement(relative.value()) & 0XFF;

    return new NumberValue(twosComplement, Size.BYTE);
  }

  public long displacementD() {
    return (NumberValue.twosComplement(((NumberValue) displacement).value())) & 0xFF;
  }
}
