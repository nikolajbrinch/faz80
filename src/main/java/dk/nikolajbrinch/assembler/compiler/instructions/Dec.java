package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;

import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Dec implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof Register register) {
      resolved =
          switch (targetOperand.addressingMode()) {
            case REGISTER -> switch (register) {
              case BC, DE, HL, SP -> ByteSource.of(0b00001011 | (Registers.ss.get(register) << 4));
              default -> ByteSource.of(0b00000101 | (Registers.r.get(register) << 3));
            };
            case REGISTER_INDIRECT -> {
              if (register == Register.HL) {
                yield ByteSource.of(() -> 0x35);
              }

              yield null;
            }
            default -> null;
          };
    }

    return resolved;
  }
}
