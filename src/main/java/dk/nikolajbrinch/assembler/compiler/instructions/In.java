package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class In implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand1.operand() instanceof Register register) {
      if (register == Register.A && operand2.operand() instanceof NumberValue number) {
        resolved = ByteSource.of(0xDB, number.value());
      } else if (operand2.operand() instanceof Register register2 && register2 == Register.C) {
        resolved = ByteSource.of(0xED, 0b01000000 | Registers.r.get(register2) << 3);
      }
    }

    return resolved;
  }
}
