package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.Register;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;

public class Bit implements InstructionGenerator {

  @Override
  public ByteSource generateRegisterToImmediate(
      Address currentAddress, ValueSupplier value, Register sourceRegister) {
    return ByteSource.of(
        0xCB,
        ByteSupplier.of(
            () ->
                0b01000000
                    | ((value.number().value() & 0b00000111) << 3)
                    | Registers.r.get(sourceRegister)));
  }

  @Override
  public ByteSource generateRegisterIndirectToImmediate(
      Address currentAddress, ValueSupplier value, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(
          0xCB, ByteSupplier.of(() -> 0b01000110 | ((value.number().value() & 0b00000111) << 3)));
    }
    return null;
  }

  @Override
  public ByteSource generateIndexedToImmediate(
      Address currentAddress, ValueSupplier value, Register sourceRegister, ByteSupplier displacement) {
    if (sourceRegister == Register.IX) {
      return ByteSource.of(
          0xDD,
          0xCB,
          displacement,
          ByteSupplier.of(() -> 0b01000110 | ((value.number().value() & 0b00000111) << 3)));
    } else if (sourceRegister == Register.IY) {
      return ByteSource.of(
          0xFD,
          0xCB,
          displacement,
          ByteSupplier.of(() -> 0b01000110 | ((value.number().value() & 0b00000111) << 3)));
    }

    return null;
  }
}
