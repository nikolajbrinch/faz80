package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import dk.nikolajbrinch.assembler.parser.Register;

public class AddressingDecoder {

  public AddressingMode decode(Object operand, boolean isIndirect, boolean hasDisplacement) {
    AddressingMode mode = AddressingMode.UNKNOWN;

    if (operand instanceof Register register) {
      mode = isIndirect ? AddressingMode.REGISTER_INDIRECT : AddressingMode.REGISTER;

      if ((register == Register.IX || register == Register.IY) && hasDisplacement) {
        mode = AddressingMode.INDEXED;
      }
    } else if (operand instanceof NumberValue number) {
      if (number.size() == Size.WORD) {
        mode = isIndirect ? AddressingMode.EXTENDED : AddressingMode.IMMEDIATE_EXTENDED;
      } else if (number.size() == Size.BYTE) {
        mode = AddressingMode.IMMEDIATE;
      }
    }

    return mode;
  }
}
