package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Or implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, Operand targetOperand, Operand sourceOperand) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> ByteSource.of(0b10110000 | Registers.r.get(targetOperand.asRegister()));
      case REGISTER_INDIRECT -> {
        if (targetOperand.asRegister() == Register.HL) {
          yield ByteSource.of(0xB69);
        }

        yield null;
      }
      case IMMEDIATE -> ByteSource.of(0xF6, targetOperand.asNumberValue().value());
      case INDEXED -> switch (targetOperand.asRegister()) {
        case IX -> ByteSource.of(0xDD, 0xB6, targetOperand.displacementD());
        case IY -> ByteSource.of(0xFD, 0xB6, targetOperand.displacementD());
        default -> null;
      };
      default -> null;
    };
  }
}
