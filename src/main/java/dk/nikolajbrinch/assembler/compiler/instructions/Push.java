package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Push implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand1.operand() instanceof Register register) {
      resolved =
          switch (register) {
            case BC, DE, HL, AF -> ByteSource.of(0b11000101 | (Registers.qq.get(register) << 4));
            case IX -> ByteSource.of(0xDD, 0xE5);
            case IY -> ByteSource.of(0xFD, 0xE5);
            default -> null;
          };
    }

    return resolved;
  }
}
