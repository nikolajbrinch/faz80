package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Add implements InstructionGenerator {

  public ByteSource generateRegisterToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister) {
    return switch (targetRegister) {
      case A -> ByteSource.of(implied1(0b10000000, Registers.r, sourceRegister));
      case IX -> ByteSource.of(0xDD, implied5(0b00001001, Registers.pp, sourceRegister));
      case IY -> ByteSource.of(0xFD, implied5(0b00001001, Registers.rr, sourceRegister));
      case HL -> ByteSource.of(implied5(0b00001001, Registers.ss, sourceRegister));
      default -> null;
    };
  }

  @Override
  public ByteSource generateImmediateToRegister(
      Address currentAddress, Register targetRegister, ValueSupplier value) {
    return switch (targetRegister) {
      case A -> ByteSource.of(0xC6, val(value));
      default -> null;
    };
  }

  @Override
  public ByteSource generateRegisterIndirectToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister) {
    return switch (targetRegister) {
      case A ->
          switch (sourceRegister) {
            case HL -> ByteSource.of(0x86);
            default -> null;
          };
      default -> null;
    };
  }

  @Override
  public ByteSource generateIndexedToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister, ByteSupplier displacement) {
    return switch (targetRegister) {
      case A ->
          switch (sourceRegister) {
            case IX -> ByteSource.of(0xDD, 0x86, displacement);
            case IY -> ByteSource.of(0xFD, 0x86, displacement);
            default -> null;
          };
      default -> null;
    };
  }
}
