package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Cp implements InstructionGenerator {

  @Override
  public ByteSource generate(
      NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> ByteSource.of(0b10111000 | Registers.r.get(targetOperand.asRegister()));
      case REGISTER_INDIRECT -> {
        if (targetOperand.asRegister() == Register.HL) {
          yield ByteSource.of(0xBE);
        }

        yield null;
      }
      case IMMEDIATE -> ByteSource.of(0xFE, targetOperand.asNumberValue().value());
      default -> null;
    };
  }
}
