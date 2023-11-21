package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;

import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;

public class Im implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof NumberValue numberValue) {
      resolved =
          switch ((int) numberValue.value()) {
            case 0 -> ByteSource.of(0xED, 0x46);
            case 1 -> ByteSource.of(0xED, 0x56);
            case 2 -> ByteSource.of(0xED, 0x5E);
            default -> null;
          };
    }

    return resolved;
  }
}
