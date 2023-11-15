package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Inc implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand1.operand() instanceof Register register) {
      resolved =
          switch (operand1.addressingMode()) {
            case REGISTER -> switch (register) {
              case BC, DE, HL, SP -> ByteSource.of(0b00000011 | (Registers.ss.get(register) << 4));
              default -> ByteSource.of(0b00000100 | (Registers.r.get(register) << 3));
            };
            case REGISTER_INDIRECT -> {
              if (register == Register.HL) {
                yield ByteSource.of(0x34);
              }
              yield null;
            }
            case INDEXED -> {
              if (register == Register.IX) {
                yield ByteSource.of(0xDD, 0x34, operand1.displacementD());
              } else if (register == Register.IY) {
                yield ByteSource.of(0xFD, 0x34, operand1.displacementD());
              }
              yield null;
            }
            default -> null;
          };
    }
    return resolved;
  }
}
