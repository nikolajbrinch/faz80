package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;
import java.util.List;

public class Sub implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, Operand targetOperand, Operand sourceOperand) {

    ByteSource resolved = null;

    resolved =
        switch (targetOperand.addressingMode()) {
          case REGISTER -> ByteSource.of(0b10010000 | Registers.r.get(targetOperand.asRegister()));
          case REGISTER_INDIRECT -> {
            if (targetOperand.asRegister() == Register.HL) {
              yield ByteSource.of(0x96);
            }

            yield null;
          }
          case IMMEDIATE -> ByteSource.of(0xD6, sourceOperand.asNumberValue().value());
          default -> null;
        };

    return resolved;
  }
}
