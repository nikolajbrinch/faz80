package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Pop implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(NumberValue currentAddress, Register register) {
    return switch (register) {
      case IX -> ByteSource.of(0xDD, 0xE1);
      case IY -> ByteSource.of(0xFD, 0xE1);
      default -> ByteSource.of(0b11000001 | (Registers.qq.get(register) << 4));
    };
  }
}
