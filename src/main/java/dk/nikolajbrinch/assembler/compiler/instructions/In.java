package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

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
