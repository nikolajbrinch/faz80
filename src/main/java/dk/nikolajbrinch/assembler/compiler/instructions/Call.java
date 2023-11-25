package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Conditions;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Condition;

public class Call implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, Operand targetOperand, Operand sourceOperand) {

    ByteSource resolved = null;

    if (targetOperand.operand() instanceof NumberValue number) {
      if (sourceOperand == null) {
        resolved = ByteSource.of(0xCD, number.lsb().value(), number.msb().value());
      } else if (sourceOperand.operand() instanceof Condition condition) {
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
