package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class And implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved =
        switch (operand1.addressingMode()) {
          case REGISTER -> ByteSource.of(0b10100000 | Registers.r.get(operand1.asRegister()));
          case REGISTER_INDIRECT -> {
            if (operand1.asRegister() == Register.HL) {
              yield ByteSource.of(0xA6);
            }
            yield null;
          }
          case IMMEDIATE -> ByteSource.of(0xE6, operand1.asNumberValue().value());
          case INDEXED -> switch (operand1.asRegister()) {
            case IX -> ByteSource.of(0xDD, 0xA6, operand1.displacementD());
            case IY -> ByteSource.of(0xFD, 0xA6, operand1.displacementD());
            default -> null;
          };
          default -> null;
        };

    if (resolved == null) {
      throw new IllegalStateException();
    }

    return resolved;
  }
}
