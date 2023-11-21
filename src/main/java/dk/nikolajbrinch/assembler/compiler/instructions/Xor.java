package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Xor implements InstructionGenerator {

  @Override
  public ByteSource generate(
      NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> ByteSource.of(0b10101000 | Registers.r.get(targetOperand.asRegister()));
      case REGISTER_INDIRECT -> {
        if (targetOperand.asRegister() == Register.HL) {
          yield ByteSource.of(0xAE);
        }

        yield null;
      }
      case IMMEDIATE -> ByteSource.of(0xEE, targetOperand.asNumberValue().value());
      case INDEXED -> switch (targetOperand.asRegister()) {
        case IX -> ByteSource.of(0xDD, 0xAE, targetOperand.displacementD());
        case IY -> ByteSource.of(0xFD, 0xAE, targetOperand.displacementD());
        default -> null;
      };
      default -> null;
    };
  }
}
