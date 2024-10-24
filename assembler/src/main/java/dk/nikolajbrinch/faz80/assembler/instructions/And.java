package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;

public class And implements InstructionGenerator {

  @Override
  public ByteSource generateTwoOperands(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER ->
          switch (targetOperand.asRegister()) {
            case Register.A -> generateSingleOperand(currentAddress, sourceOperand);
            default -> null;
          };
      default -> null;
    };
  }

  @Override
  public ByteSource generateRegister(Address currentAddress, Register register) {
    return ByteSource.of(0b10100000 | Registers.r.get(register));
  }

  @Override
  public ByteSource generateRegisterIndirect(Address currentAddress, Register register) {
    return switch (register) {
      case HL -> ByteSource.of(0xA6);
      default -> null;
    };
  }

  @Override
  public ByteSource generateImmediate(Address currentAddress, ValueSupplier value) {
    return ByteSource.of(0xE6, val(value));
  }

  @Override
  public ByteSource generateIndexed(Address currentAddress, Register register, ByteSupplier displacement) {
    return switch (register) {
      case IX -> ByteSource.of(0xDD, 0xA6, displacement);
      case IY -> ByteSource.of(0xFD, 0xA6, displacement);
      default -> null;
    };
  }
}
