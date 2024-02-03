package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;

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
