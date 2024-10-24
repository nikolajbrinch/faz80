package dk.nikolajbrinch.faz80.assembler.operands;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;
import dk.nikolajbrinch.faz80.parser.base.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.base.values.NumberValue.Size;

public class OperandUtil {

  public static long displacementE(NumberValue number, Address address) {
    NumberValue relative = number.subtract(address.logicalAddress());

    relative = relative.subtract(NumberValue.create(2));

    if (relative.value() < -126) {
      throw new IllegalDisplacementException(
          relative,
          "Displacement e too low: "
              + relative
              + "[current address: "
              + address.logicalAddress().value()
              + ", jump address: "
              + number.value()
              + "]");
    }
    if (relative.value() > 129) {
      throw new IllegalDisplacementException(
          relative,
          "Displacement e too high: "
              + relative
              + "[current address: "
              + address.logicalAddress().value()
              + ", jump address: "
              + number.value()
              + "]");
    }

    long twosComplement = NumberValue.twosComplement(relative.value()) & 0XFF;

    return new NumberValue(twosComplement, Size.BYTE).value() & 0xFF;
  }

  public static long displacementE(ValueSupplier valueSupplier, Address address) {
    return displacementE(valueSupplier.number(), address);
  }

  public static long displacementD(NumberValue number) {
    return (NumberValue.twosComplement(number.value())) & 0xFF;
  }

  public static long displacementD(ValueSupplier valueSupplier) {
    return displacementD(valueSupplier.number());
  }
}
