package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.parser.Register;

public class Ex implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {

    if (operand1.operand() instanceof Register register1
        && operand2.operand() instanceof Register register2) {

      return switch (operand1.addressingMode()) {
        case REGISTER -> {
          if (register1 == Register.DE && register2 == Register.HL) {
            yield ByteSource.of(0xEB);
          } else if (register1 == Register.AF && register2 == Register.AF_BANG) {
            yield ByteSource.of(0x08);
          }
          yield null;
        }
        case REGISTER_INDIRECT -> {
          if (register1 == Register.SP) {
            yield switch (register2) {
              case Register.HL -> ByteSource.of(0xE3);
              case Register.IX -> ByteSource.of(0xDD, 0xE3);
              case Register.IY -> ByteSource.of(0xFD, 0xE3);
              default -> null;
            };
          }
          yield null;
        }
        default -> null;
      };
    }

    return null;
  }
}
