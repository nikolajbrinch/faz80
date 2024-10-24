package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.base.Register;

public class Inc implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof Register register) {
      resolved =
          switch (targetOperand.addressingMode()) {
            case REGISTER -> switch (register) {
              case BC, DE, HL, SP -> ByteSource.of(0b00000011 | (Registers.ss.get(register) << 4));
              case IX -> ByteSource.of(0xDD, 0x23);
              case IY -> ByteSource.of(0xFD, 0x23);
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
                yield ByteSource.of(0xDD, 0x34, targetOperand.displacementD());
              } else if (register == Register.IY) {
                yield ByteSource.of(0xFD, 0x34, targetOperand.displacementD());
              }
              yield null;
            }
            default -> null;
          };
    }
    return resolved;
  }
}
