package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Xor implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    return switch (operand1.addressingMode()) {
      case REGISTER -> ByteSource.of(0b10101000 | Registers.r.get(operand1.asRegister()));
      case REGISTER_INDIRECT -> {
        if (operand1.asRegister() == Register.HL) {
          yield ByteSource.of(0xAE);
        }

        yield null;
      }
      case IMMEDIATE -> ByteSource.of(0xEE, operand1.asNumberValue().value());
      case INDEXED -> switch (operand1.asRegister()) {
        case IX -> ByteSource.of(0xDD, 0xAE, operand1.displacementD());
        case IY -> ByteSource.of(0xFD, 0xAE, operand1.displacementD());
        default -> null;
      };
      default -> null;
    };
  }
}
