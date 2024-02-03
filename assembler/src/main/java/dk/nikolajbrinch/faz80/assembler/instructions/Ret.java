package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.operands.Conditions;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.parser.Condition;

public class Ret implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    ByteSource resolved = null;

    if (targetOperand == null) {
      resolved = ByteSource.of(() -> 0xC9);
    } else if (targetOperand.operand() instanceof Condition condition) {
      resolved = ByteSource.of(() -> 0b11000000 | (Conditions.cc.get(condition) << 3));
    }

    return resolved;
  }
}
