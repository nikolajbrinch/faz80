package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.AddressingMode;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;

public class Adc implements InstructionGenerator {

  /**
   * Shorthand for generating a single operand instruction
   * @param currentAddress
   * @param operand
   * @return
   */
  @Override
  public ByteSource generateSingleOperand(Address currentAddress, EvaluatedOperand operand) {
    return generateTwoOperands(currentAddress, new EvaluatedOperand(Register.A, null,false, AddressingMode.REGISTER), operand);
  }

  @Override
  public ByteSource generateRegisterToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister) {
    return switch (targetRegister) {
      case A -> ByteSource.of(implied1(0b10001000, Registers.r, sourceRegister));
      case HL -> ByteSource.of(0xED, implied5(0b01001010, Registers.ss, sourceRegister));
      default -> null;
    };
  }

  @Override
  public ByteSource generateImmediateToRegister(
      Address currentAddress, Register targetRegister, ValueSupplier value) {
    return switch (targetRegister) {
      case A -> ByteSource.of(0xCE, val(value));
      default -> null;
    };
  }

  @Override
  public ByteSource generateRegisterIndirectToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister) {
    if (targetRegister == Register.A && sourceRegister == Register.HL) {
      return ByteSource.of(0x8E);
    }

    return null;
  }

  @Override
  public ByteSource generateIndexedToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister, ByteSupplier displacement) {
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
