package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Conditions;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Condition;

public class Ret implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand1 == null) {
      resolved = ByteSource.of(() -> 0xC9);
    } else if (operand1.operand() instanceof Condition condition) {
      resolved = ByteSource.of(() -> 0b11000000 | (Conditions.cc.get(condition) << 3));
    }

    return resolved;
  }
}
