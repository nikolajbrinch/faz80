package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Sbc implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand1.operand() instanceof Register register) {
      if (register == Register.A) {
        resolved =
            switch (operand2.addressingMode()) {
              case REGISTER -> ByteSource.of(0b10011000 | Registers.r.get(operand2.asRegister()));
              case REGISTER_INDIRECT -> {
                if (operand2.asRegister() == Register.HL) {
                  yield ByteSource.of(() -> 0x9E);
                }
                yield null;
              }
              case INDEXED -> null;
              case IMMEDIATE -> ByteSource.of(0xDE, operand2.asNumberValue().value());
              default -> null;
            };
      } else if (register == Register.HL) {
        resolved = ByteSource.of(0xED, 0b01000010 | Registers.ss.get(operand2.asRegister()));
      }
    }

    if (resolved == null) {
      throw new IllegalStateException();
    }

    return resolved;
  }
}
