package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Conditions;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.parser.Condition;

public class Call implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {

    ByteSource resolved = null;

    if (operand1.operand() instanceof NumberValue number) {
      if (operand2 == null) {
        resolved = ByteSource.of(0xCD, number.lsb().value(), number.msb().value());
      } else if (operand2.operand() instanceof Condition condition) {
        resolved =
            ByteSource.of(
                0b11000100 | (Conditions.cc.get(condition) << 3),
                number.lsb().value(),
                number.msb().value());
      }
    }

    return resolved;
  }
}
