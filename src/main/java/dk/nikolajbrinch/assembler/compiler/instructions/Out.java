package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Out implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand1.operand() instanceof NumberValue number) {
      if (operand2.operand() instanceof Register register2 && register2 == Register.A) {
        resolved = ByteSource.of(0xD3, number.value());
      }
    } else if (operand1.operand() instanceof Register register1) {
      if (register1 == Register.C && operand2.operand() instanceof Register register2) {
        resolved = ByteSource.of(0xED, 0b01000001 | Registers.r.get(register2) << 3);
      }
    }

    return resolved;
  }
}
