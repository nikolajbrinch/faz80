package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;

public class Djnz implements InstructionGenerator {

  @Override
  public ByteSource generate(
      Address currentAddress,
      EvaluatedOperand targetOperand,
      EvaluatedOperand sourceOperand,
      EvaluatedOperand extraOperand) {
    return ByteSource.of(0x10, ByteSupplier.of(() -> targetOperand.displacementE(currentAddress)));
  }
}
