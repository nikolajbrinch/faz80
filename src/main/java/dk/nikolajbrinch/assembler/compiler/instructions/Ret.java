package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Conditions;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.parser.Condition;

public class Ret implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, Operand targetOperand, Operand sourceOperand) {
    ByteSource resolved = null;

    if (targetOperand == null) {
      resolved = ByteSource.of(() -> 0xC9);
    } else if (targetOperand.operand() instanceof Condition condition) {
      resolved = ByteSource.of(() -> 0b11000000 | (Conditions.cc.get(condition) << 3));
    }

    return resolved;
  }
}
