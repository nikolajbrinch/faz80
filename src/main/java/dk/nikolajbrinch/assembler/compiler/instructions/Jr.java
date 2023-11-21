package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;

import dk.nikolajbrinch.assembler.compiler.operands.AddressingMode;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Condition;

public class Jr implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    ByteSource resolved = null;

    if (sourceOperand == null) {
      if (targetOperand.addressingMode() == AddressingMode.IMMEDIATE) {
        return ByteSource.of(0x18, targetOperand.displacementE(currentAddress).value());
      }
    } else {
      if (targetOperand.operand() instanceof Condition condition) {
        return switch (condition) {
          case NZ -> ByteSource.of(0x20, sourceOperand.displacementE(currentAddress).value());
          case Z -> ByteSource.of(0x28, sourceOperand.displacementE(currentAddress).value());
          case NC -> ByteSource.of(0x30, sourceOperand.displacementE(currentAddress).value());
          case C -> ByteSource.of(0x38, sourceOperand.displacementE(currentAddress).value());
          default -> null;
        };
      }
    }

    return resolved;
  }
}
