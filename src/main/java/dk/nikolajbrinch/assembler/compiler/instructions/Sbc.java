package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Sbc implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, Operand targetOperand, Operand sourceOperand) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof Register register) {
      if (register == Register.A) {
        resolved =
            switch (sourceOperand.addressingMode()) {
              case REGISTER -> ByteSource.of(0b10011000 | Registers.r.get(sourceOperand.asRegister()));
              case REGISTER_INDIRECT -> {
                if (sourceOperand.asRegister() == Register.HL) {
                  yield ByteSource.of(() -> 0x9E);
                }
                yield null;
              }
              case INDEXED -> null;
              case IMMEDIATE -> ByteSource.of(0xDE, sourceOperand.asNumberValue().value());
              default -> null;
            };
      } else if (register == Register.HL) {
        resolved = ByteSource.of(0xED, 0b01000010 | Registers.ss.get(sourceOperand.asRegister()));
      }
    }

    if (resolved == null) {
      throw new IllegalStateException();
    }

    return resolved;
  }
}
