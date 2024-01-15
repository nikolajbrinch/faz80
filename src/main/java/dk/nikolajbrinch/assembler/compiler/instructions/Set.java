package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Set implements InstructionGenerator {

  /**
   * SET b, m
   *
   * @param currentAddress
   * @param numberValue
   * @param sourceRegister
   * @return
   */
  @Override
  public ByteSource generateRegisterToImmediate(
      Address currentAddress, NumberValue numberValue, Register sourceRegister) {
    return ByteSource.of(
        0xCB,
        0b11000000 | ((numberValue.value() & 0b00000111) << 3) | Registers.r.get(sourceRegister));
  }

  /**
   * SET b, (HL)
   *
   * @param currentAddress
   * @param numberValue
   * @param register
   * @return
   */
  @Override
  public ByteSource generateRegisterIndirectToImmediate(
      Address currentAddress, NumberValue numberValue, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(0xCB, 0b11000110 | ((numberValue.value() & 0b00000111) << 3));
    }

    return null;
  }

  /**
   * SET b, (IX+d), SET b, (IY+d)
   *
   * @param currentAddress
   * @param numberValue
   * @param sourceRegister
   * @param displacement
   * @return
   */
  @Override
  public ByteSource generateIndexedToImmediate(
      Address currentAddress, NumberValue numberValue, Register sourceRegister, long displacement) {
    if (sourceRegister == Register.IX) {
      return ByteSource.of(
          0xDD, 0xCB, displacement, 0b11000110 | ((numberValue.value() & 0b00000111) << 3));
    } else if (sourceRegister == Register.IY) {
      return ByteSource.of(
          0xFD, 0xCB, displacement, 0b11000110 | ((numberValue.value() & 0b00000111) << 3));
    }

    return null;
  }
}
