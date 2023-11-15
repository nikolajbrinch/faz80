package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import java.util.Map;

public class Rst implements InstructionGenerator {

  private static Map<Integer, Integer> p =
      Map.of(
          0x00, 0b000, 0x08, 0b001, 0x10, 0b010, 0x18, 0b011, 0x20, 0b100, 0x28, 0b101, 0x30, 0b110,
          0x38, 0b111);

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand1.operand() instanceof NumberValue number) {
      resolved = ByteSource.of(0b11000111 | p.get((int) number.value()));
    }

    return resolved;
  }
}
