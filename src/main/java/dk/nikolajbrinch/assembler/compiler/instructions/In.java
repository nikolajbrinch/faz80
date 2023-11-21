package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;

import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class In implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    ByteSource resolved = null;

    if (targetOperand.operand() instanceof Register register) {
      if (register == Register.A && sourceOperand.operand() instanceof NumberValue number) {
        resolved = ByteSource.of(0xDB, number.value());
      } else if (sourceOperand.operand() instanceof Register register2 && register2 == Register.C) {
        resolved = ByteSource.of(0xED, 0b01000000 | Registers.r.get(register2) << 3);
      }
    }

    return resolved;
  }
}
