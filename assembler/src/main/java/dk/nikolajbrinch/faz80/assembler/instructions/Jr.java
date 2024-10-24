package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.base.Condition;

public class Jr implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    if (sourceOperand == null) {
      return switch (targetOperand.addressingMode()) {
        case IMMEDIATE, IMMEDIATE_EXTENDED ->
            ByteSource.of(0x18, ByteSupplier.of(() -> targetOperand.displacementE(currentAddress)));
        default -> null;
      };
    } else {
      if (targetOperand.operand() instanceof Condition condition) {
        return switch (condition) {
          case NZ -> ByteSource.of(0x20,
              ByteSupplier.of(() -> sourceOperand.displacementE(currentAddress)));
          case Z -> ByteSource.of(0x28, ByteSupplier.of(() -> sourceOperand.displacementE(currentAddress)));
          case NC -> ByteSource.of(0x30, ByteSupplier.of(() -> sourceOperand.displacementE(currentAddress)));
          case C -> ByteSource.of(0x38, ByteSupplier.of(() -> sourceOperand.displacementE(currentAddress)));
          default -> null;
        };
      }
    }

    return null;
  }
}
