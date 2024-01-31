package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Set implements InstructionGenerator {

  /**
   * SET b, m
   *
   * @param currentAddress
   * @param value
   * @param sourceRegister
   * @return
   */
  @Override
  public ByteSource generateRegisterToImmediate(
      Address currentAddress, ValueSupplier value, Register sourceRegister) {
    return ByteSource.of(
        0xCB,
        ByteSupplier.of(
            () ->
                0b11000000
                    | ((value.number().value() & 0b00000111) << 3)
                    | Registers.r.get(sourceRegister)));
  }

  /**
   * SET b, (HL)
   *
   * @param currentAddress
   * @param value
   * @param register
   * @return
   */
  @Override
  public ByteSource generateRegisterIndirectToImmediate(
      Address currentAddress, ValueSupplier value, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(
          0xCB, ByteSupplier.of(() -> 0b11000110 | ((value.number().value() & 0b00000111) << 3)));
    }

    return null;
  }

  /**
   * SET b, (IX+d), SET b, (IY+d)
   *
   * @param currentAddress
   * @param value
   * @param sourceRegister
   * @param displacement
   * @return
   */
  @Override
  public ByteSource generateIndexedToImmediate(
      Address currentAddress, ValueSupplier value, Register sourceRegister, ByteSupplier displacement) {
    if (sourceRegister == Register.IX) {
      return ByteSource.of(
          0xDD,
          0xCB,
          displacement,
          ByteSupplier.of(() -> 0b11000110 | ((value.number().value() & 0b00000111) << 3)));
    } else if (sourceRegister == Register.IY) {
      return ByteSource.of(
          0xFD,
          0xCB,
          displacement,
          ByteSupplier.of(() -> 0b11000110 | ((value.number().value() & 0b00000111) << 3)));
    }

    return null;
  }

  /**
   * SET r, b, (IX+d)
   *
   * @param currentAddress
   * @param register
   * @param value
   * @param displacement
   * @param copyRegister
   * @return
   */
  @Override
  public ByteSource generateImmediateToIndexedCopyRegister(
      Address currentAddress,
      Register register,
      ValueSupplier value,
      ByteSupplier displacement,
      Register copyRegister) {
    return switch (register) {
      case IX ->
          ByteSource.of(
              0xDD,
              0xCB,
              displacement,
              ByteSupplier.of(
                  () ->
                      0b11000000 | Registers.r.get(copyRegister) | (value.number().value() << 3)));
      case IY ->
          ByteSource.of(
              0xFD,
              0xCB,
              displacement,
              ByteSupplier.of(
                  () ->
                      0b11000000 | Registers.r.get(copyRegister) | (value.number().value() << 3)));
      default -> null;
    };
  }
}
