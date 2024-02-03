package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.Register;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;

public class In implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof Register register) {
      if (register == Register.A && sourceOperand.operand() instanceof ValueSupplier value) {
        resolved = ByteSource.of(0xDB, val(value));
      } else if (sourceOperand.operand() instanceof Register register2 && register2 == Register.C) {
        resolved = ByteSource.of(0xED, 0b01000000 | Registers.r.get(register2) << 3);
      }
    }

    return resolved;
  }
}
