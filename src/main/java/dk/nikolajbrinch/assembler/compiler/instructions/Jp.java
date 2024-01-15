package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Conditions;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.parser.Condition;

public class Jp implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, Operand targetOperand, Operand sourceOperand) {
    return switch (targetOperand.addressingMode()) {
      case IMMEDIATE_EXTENDED -> ByteSource.of(
          0xC3,
          targetOperand.asNumberValue().lsb().value(),
          targetOperand.asNumberValue().msb().value());
      case REGISTER_INDIRECT -> switch (targetOperand.asRegister()) {
        case HL -> ByteSource.of(0xE9);
        case IX -> ByteSource.of(0xDD, 0xE9);
        case IY -> ByteSource.of(0xFD, 0xE9);
        default -> null;
      };
      default -> {
        if (targetOperand.operand() instanceof Condition condition) {
          yield ByteSource.of(
              0b11000010 | Conditions.cc.get(condition),
              targetOperand.asNumberValue().lsb().value(),
              targetOperand.asNumberValue().msb().value());
        }

        yield null;
      }
    };
  }
}
