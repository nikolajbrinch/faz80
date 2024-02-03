package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.Register;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;

public class Out implements InstructionGenerator {

  @Override
  public ByteSource generate(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperands) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof ValueSupplier value) {
      if (sourceOperand.operand() instanceof Register register2 && register2 == Register.A) {
        resolved = ByteSource.of(0xD3, val(value));
      }
    } else if (targetOperand.operand() instanceof Register register1) {
      if (register1 == Register.C && sourceOperand.operand() instanceof Register register2) {
        resolved = ByteSource.of(0xED, 0b01000001 | Registers.r.get(register2) << 3);
      }
    }

    return resolved;
  }
}
