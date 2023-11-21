package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;

public class Adc implements InstructionGenerator {

  @Override
  public ByteSource generateRegisterToRegister(
      NumberValue currentAddress, Register targetRegister, Register sourceRegister) {
    return switch (targetRegister) {
      case A -> 
          ByteSource.of(InstructionGenerator.implied1(0b10001000, Registers.r, sourceRegister));
      case HL -> 
          ByteSource.of(
              0xED, InstructionGenerator.implied5(0b01001010, Registers.ss, sourceRegister));
      default -> null;
    };
  }

  @Override
  public ByteSource generateImmediateToRegister(
      NumberValue currentAddress, Register targetRegister, NumberValue numberValue) {
    return switch (targetRegister) {
      case A -> ByteSource.of(0xCE, numberValue.value());
      default -> null;
    };
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
      NumberValue currentAddress,
      Register targetRegister,
      Register sourceRegister,
      long displacement) {
    if (targetRegister == Register.A) {
      return switch (sourceRegister) {
        case IX -> ByteSource.of(0xDD, 0x8E, displacement);
        case IY -> ByteSource.of(0xFD, 0x8E, displacement);
        default -> null;
      };
    }

    return null;
  }
}
