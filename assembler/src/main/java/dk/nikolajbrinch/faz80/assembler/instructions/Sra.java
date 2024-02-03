package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.Register;

public class Sra implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(Address currentAddress, Register register) {
    return ByteSource.of(0xCB, implied1(0b00101000, Registers.r, register));
  }

  @Override
  public ByteSource generateRegisterIndirect(Address currentAddress, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(0xCB, 0x2E);
    }

    return null;
  }

  @Override
  public ByteSource generateIndexed(
      Address currentAddress, Register targetRegister, ByteSupplier displacement) {
    return switch (targetRegister) {
      case IX -> ByteSource.of(0xDD, 0xCB, displacement, 0x2E);
      case IY -> ByteSource.of(0xFD, 0xCB, displacement, 0x2E);
      default -> null;
    };
  }
}
