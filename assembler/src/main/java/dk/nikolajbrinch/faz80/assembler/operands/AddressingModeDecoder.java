package dk.nikolajbrinch.faz80.assembler.operands;

import dk.nikolajbrinch.faz80.parser.base.values.NumberValue.Size;
import dk.nikolajbrinch.faz80.parser.base.Register;

public class AddressingModeDecoder {

  public AddressingMode decodeRegister(
      Register register, boolean isIndirect, boolean hasDisplacement) {
    AddressingMode mode =
        mode = isIndirect ? AddressingMode.REGISTER_INDIRECT : AddressingMode.REGISTER;

    if ((register == Register.IX || register == Register.IY) && hasDisplacement) {
      mode = AddressingMode.INDEXED;
    }

    return mode;
  }

  public AddressingMode decodeExpression(Size size, boolean isIndirect) {
    AddressingMode mode = AddressingMode.UNKNOWN;
    if (size == Size.WORD) {
      mode = isIndirect ? AddressingMode.EXTENDED : AddressingMode.IMMEDIATE_EXTENDED;
    } else if (size == Size.BYTE) {
      mode = AddressingMode.IMMEDIATE;
    }

    return mode;
  }
}
