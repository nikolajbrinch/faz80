package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Rrc implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(Address currentAddress, Register register) {
    return ByteSource.of(0xCB, InstructionGenerator.implied1(0b00001000, Registers.r, register));
  }

  @Override
  public ByteSource generateRegisterIndirect(Address currentAddress, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(0xCB, 0x0E);
    }

    return null;
  }

  @Override
  public ByteSource generateIndexed(
      Address currentAddress, Register targetRegister, long displacement) {
    return switch (targetRegister) {
      case IX -> ByteSource.of(0xDD, 0xCB, displacement, 0x0E);
      case IY -> ByteSource.of(0xFD, 0xCB, displacement, 0x0E);
      default -> null;
    };
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
      Address currentAddress, Register targetRegister, long displacement, Register sourceRegister) {
    return switch (targetRegister) {
      case IX -> ByteSource.of(
          0xDD,
          0xCB,
          displacement,
          InstructionGenerator.implied1(0b00001000, Registers.r, sourceRegister));
      case IY -> ByteSource.of(
          0xFD,
          0xCB,
          displacement,
          InstructionGenerator.implied1(0b00001000, Registers.r, sourceRegister));
      default -> null;
    };
  }
}
