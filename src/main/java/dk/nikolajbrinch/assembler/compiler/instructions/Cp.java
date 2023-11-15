package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Cp implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    return switch (operand1.addressingMode()) {
      case REGISTER -> ByteSource.of(0b10111000 | Registers.r.get(operand1.asRegister()));
      case REGISTER_INDIRECT -> {
        if (operand1.asRegister() == Register.HL) {
          yield ByteSource.of(0xBE);
        }

        yield null;
      }
      case IMMEDIATE -> ByteSource.of(0xFE, operand1.asNumberValue().value());
      default -> null;
    };
  }
}
