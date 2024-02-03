package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.Register;

public class Or implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
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
