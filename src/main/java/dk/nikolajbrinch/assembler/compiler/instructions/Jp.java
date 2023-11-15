package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Conditions;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.parser.Condition;

public class Jp implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    return switch (operand1.addressingMode()) {
      case IMMEDIATE_EXTENDED -> ByteSource.of(
          0xC3, operand1.asNumberValue().lsb().value(), operand1.asNumberValue().msb().value());
      case REGISTER_INDIRECT -> switch (operand1.asRegister()) {
        case HL -> ByteSource.of(0xE9);
        case IX -> ByteSource.of(0xDD, 0xE9);
        case IY -> ByteSource.of(0xFD, 0xE9);
        default -> null;
      };
      default -> {
        if (operand1.operand() instanceof Condition condition) {
          yield ByteSource.of(
              0b11000010 | Conditions.cc.get(condition),
              operand1.asNumberValue().lsb().value(),
              operand1.asNumberValue().msb().value());
        }

        yield null;
      }
    };
  }
}
