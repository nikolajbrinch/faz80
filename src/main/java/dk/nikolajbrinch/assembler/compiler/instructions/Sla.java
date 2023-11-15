package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Sla implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(NumberValue currentAddress, Register register) {
    return ByteSource.of(0xCB, InstructionGenerator.implied1(0x20, Registers.r, register));
  }

  @Override
  public ByteSource generateRegisterIndirect(NumberValue currentAddress, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(0xCB, 0x26);
    }

    return null;
  }

  @Override
  public ByteSource generateIndexed(NumberValue currentAddress, Operand targetIndex) {
    Register targetRegister = targetIndex.asRegister();

    if (targetRegister == Register.IX) {
      return ByteSource.of(0xDD, 0xCB, targetIndex.displacementD(), 0x26);
    } else if (targetRegister == Register.IY) {
      return ByteSource.of(0xFD, 0xCB, targetIndex.displacementD(), 0x26);
    }

    return null;
  }

  /**
   * Undocumented
   *
   * @param currentAddress
   * @param targetIndex
   * @param register
   * @return
   */
  @Override
  public ByteSource generateRegisterToIndexed(
      NumberValue currentAddress, Operand targetIndex, Register register) {
    Register targetRegister = targetIndex.asRegister();

    if (targetRegister == Register.IX) {
      return ByteSource.of(
          0xDD,
          0xCB,
          targetIndex.displacementD(),
          InstructionGenerator.implied1(0x20, Registers.r, register));
    } else if (targetRegister == Register.IY) {
      return ByteSource.of(
          0xFD,
          0xCB,
          targetIndex.displacementD(),
          InstructionGenerator.implied1(0x20, Registers.r, register));
    }

    return null;
  }
}
