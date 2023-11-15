package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteFunction;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Adc implements InstructionGenerator {

  @Override
  public ByteSource generateRegisterToRegister(
      NumberValue currentAddress, Register targetRegister, Register sourceRegister) {
    if (targetRegister == Register.A) {
      return ByteSource.of(InstructionGenerator.implied1(0b10001000, Registers.r, sourceRegister));
    } else if (targetRegister == Register.HL) {
      return ByteSource.of(
          0xED, InstructionGenerator.implied5(0b01001010, Registers.ss, sourceRegister));
    }

    return null;
  }

  @Override
  public ByteSource generateImmediateToRegister(
      NumberValue currentAddress, Register targetRegister, NumberValue numberValue) {
    if (targetRegister == Register.A) {
      return ByteSource.of(0xCE, numberValue.value());
    }

    return null;
  }

  @Override
  public ByteSource generateRegisterIndirectToRegister(
      NumberValue currentAddress, Register targetRegister, Register sourceRegister) {
    if (targetRegister == Register.A && sourceRegister == Register.HL) {
      return ByteSource.of(0x8E);
    }

    return null;
  }

  @Override
  public ByteSource generateIndexedToRegister(
      NumberValue currentAddress, Register targetRegister, Operand sourceIndex) {
    if (targetRegister == Register.A) {
      Register sourceRegister = sourceIndex.asRegister();

      if (sourceRegister == Register.IX) {
        return ByteSource.of(0xDD, 0x8E, sourceIndex.displacementD());
      } else if (sourceRegister == Register.IY) {
        return ByteSource.of(0xFD, 0x8E, sourceIndex.displacementD());
      }
    }

    return null;
  }
}
