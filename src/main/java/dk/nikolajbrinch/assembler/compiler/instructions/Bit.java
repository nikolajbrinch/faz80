package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Bit implements InstructionGenerator {

  @Override
  public ByteSource generateRegisterToImmediate(
      NumberValue currentAddress, NumberValue numberValue, Register sourceRegister) {
    return ByteSource.of(
        0xCB,
        0b01000000 | ((numberValue.value() & 0b00000111) << 3) | Registers.r.get(sourceRegister));
  }

  @Override
  public ByteSource generateRegisterIndirectToImmediate(
      NumberValue currentAddress, NumberValue numberValue, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(0xCB, 0b01000110 | ((numberValue.value() & 0b00000111) << 3));
    }
    return null;
  }

  @Override
  public ByteSource generateIndexedToImmediate(
      NumberValue currentAddress,
      NumberValue numberValue,
      Register sourceRegister,
      long displacement) {
    if (sourceRegister == Register.IX) {
      return ByteSource.of(
          0xDD, 0xCB, displacement, 0b01000110 | ((numberValue.value() & 0b00000111) << 3));
    } else if (sourceRegister == Register.IY) {
      return ByteSource.of(
          0xFD, 0xCB, displacement, 0b01000110 | ((numberValue.value() & 0b00000111) << 3));
    }

    return null;
  }
}
