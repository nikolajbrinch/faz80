package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;
import java.util.Map;

public class Rst implements InstructionGenerator {

  private static final Map<Integer, Integer> p =
      Map.of(
          0x00, 0b000, 0x08, 0b001, 0x10, 0b010, 0x18, 0b011, 0x20, 0b100, 0x28, 0b101, 0x30, 0b110,
          0x38, 0b111);

  @Override
  public ByteSource generate(Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof ValueSupplier value) {
      resolved = ByteSource.of(
          ByteSupplier.of(() -> 0b11000111 | p.get((int) value.number().value())));
    }

    return resolved;
  }
}
