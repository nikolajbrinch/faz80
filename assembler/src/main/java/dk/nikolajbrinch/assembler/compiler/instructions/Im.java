package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;

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
