package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Sra implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(NumberValue currentAddress, Register register) {
    return ByteSource.of(0xCB, InstructionGenerator.implied1(0b00101000, Registers.r, register));
  }

  @Override
  public ByteSource generateRegisterIndirect(NumberValue currentAddress, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(0xCB, 0x2E);
    }

    return null;
  }

  @Override
  public ByteSource generateIndexed(
      NumberValue currentAddress, Register targetRegister, long displacement) {
    return switch (targetRegister) {
      case IX -> ByteSource.of(0xDD, 0xCB, displacement, 0x2E);
      case IY -> ByteSource.of(0xFD, 0xCB, displacement, 0x2E);
      default -> null;
    };
  }
}
