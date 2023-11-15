package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.AddressingMode;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.parser.Condition;

public class Jr implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand2 == null) {
      if (operand1.addressingMode() == AddressingMode.IMMEDIATE) {
        return ByteSource.of(0x18, operand1.displacementE(currentAddress).value());
      }
    } else {
      if (operand1.operand() instanceof Condition condition) {
        return switch (condition) {
          case NZ -> ByteSource.of(0x20, operand2.displacementE(currentAddress).value());
          case Z -> ByteSource.of(0x28, operand2.displacementE(currentAddress).value());
          case NC -> ByteSource.of(0x30, operand2.displacementE(currentAddress).value());
          case C -> ByteSource.of(0x38, operand2.displacementE(currentAddress).value());
          default -> null;
        };
      }
    }

    return resolved;
  }
}
