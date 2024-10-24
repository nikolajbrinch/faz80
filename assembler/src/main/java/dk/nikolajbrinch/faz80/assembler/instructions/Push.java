package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.base.Register;

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
