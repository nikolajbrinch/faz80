package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;

public class Im implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof ValueSupplier value) {
      resolved = ByteSource.of(0xED, ByteSupplier.of(() ->
          switch ((int) value.number().value()) {
            case 0 -> 0x46;
            case 1 -> 0x56;
            case 2 -> 0x5E;
            default -> throw new IllegalArgumentException("Value not supported for instruction");
          }));
    }

    return resolved;
  }
}
