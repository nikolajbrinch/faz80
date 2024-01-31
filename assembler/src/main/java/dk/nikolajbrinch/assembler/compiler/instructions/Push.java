package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Push implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(Address currentAddress, Register register) {
    return switch (register) {
      case IX -> ByteSource.of(0xDD, 0xE5);
      case IY -> ByteSource.of(0xFD, 0xE5);
      default -> ByteSource.of(0b11000101 | (Registers.qq.get(register) << 4));
    };
  }
}
