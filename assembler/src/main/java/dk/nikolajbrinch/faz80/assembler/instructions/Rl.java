package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.base.Register;

public class Rl implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(Address currentAddress, Register register) {
    return ByteSource.of(0xCB, implied1(0b00010000, Registers.r, register));
  }

  @Override
  public ByteSource generateRegisterIndirect(Address currentAddress, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(0xCB, 0x16);
    }

    return null;
  }

  @Override
  public ByteSource generateIndexed(
      Address currentAddress, Register targetRegister, ByteSupplier displacement) {
    if (targetRegister == Register.IX) {
      return ByteSource.of(0xDD, 0xCB, displacement, 0x16);
    } else if (targetRegister == Register.IY) {
      return ByteSource.of(0xFD, 0xCB, displacement, 0x16);
    }

    return null;
  }

  /**
   * Undocumented
   *
   * @param currentAddress
   * @param targetRegister
   * @param displacement
   * @param sourceRegister
   * @return
   */
  @Override
  public ByteSource generateRegisterToIndexed(
      Address currentAddress, Register targetRegister, ByteSupplier displacement, Register sourceRegister) {
    return switch (targetRegister) {
      case IX -> ByteSource.of(
          0xDD,
          0xCB,
          displacement,
          implied1(0b00010000, Registers.r, sourceRegister));
      case IY -> ByteSource.of(
          0xFD,
          0xCB,
          displacement,
          implied1(0b00010000, Registers.r, sourceRegister));
      default -> null;
    };
  }
}
