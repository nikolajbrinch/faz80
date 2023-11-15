package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.NumberValue.Size;
import dk.nikolajbrinch.assembler.compiler.operands.AddressingMode;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Ld implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved =
        switch (operand1.addressingMode()) {
          case REGISTER -> resolveOperandToRegister(operand1, operand2);
          case REGISTER_INDIRECT -> resolveOperandToRegisterIndirect(
              operand1.asRegister(), operand2);
          case INDEXED -> resolveOperandToRegisterIndexed(operand1, operand2);
          case EXTENDED -> resolveOperandToExtended(operand1.asNumberValue(), operand2);
          default -> null;
        };

    if (resolved == null) {
      throw new IllegalStateException();
    }

    return resolved;
  }

  private static ByteSource resolveOperandToRegister(Operand operand1, Operand operand2) {
    return switch (operand2.addressingMode()) {
      case REGISTER -> resolveOperandRegisterToRegister(
          operand1.asRegister(), operand2.asRegister());
      case REGISTER_INDIRECT -> resolveOperandRegisterIndirectToRegister(
          operand1.asRegister(), operand2.asRegister());
      case EXTENDED -> resolveOperandExtendedToRegister(
          operand1.asNumberValue(), operand2.asRegister());
      case IMMEDIATE -> {
        /*
         * Kludge, please implement proper identification of addressing
         */
        if (operand1.asRegister().size() == Size.WORD) {
          yield resolveOperandWordValueToRegister(operand1.asRegister(), operand2.asNumberValue());
        }

        yield resolveOperandByteValueToRegister(operand1.asRegister(), operand2.asNumberValue());
      }
      case IMMEDIATE_EXTENDED -> resolveOperandWordValueToRegister(
          operand1.asRegister(), operand2.asNumberValue());
      case INDEXED -> resolveOperandRegisterIndexedToRegister(operand1.asRegister(), operand2);
      default -> null;
    };
  }

  private static ByteSource resolveOperandToRegisterIndirect(Register operand1, Operand operand2) {
    return switch (operand2.addressingMode()) {
      case REGISTER -> resolveOperandRegisterToRegisterIndirect(operand1, operand2.asRegister());
      case IMMEDIATE -> resolveOperandByteValueToRegisterIndirect(
          operand1, operand2.asNumberValue());
      default -> null;
    };
  }

  private static ByteSource resolveOperandToRegisterIndexed(Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand2.addressingMode() == AddressingMode.IMMEDIATE) {
      resolved = resolveOperandByteValueToRegisterIndexed(operand1, operand2.asNumberValue());
    }

    return resolved;
  }

  public static ByteSource resolveOperandToExtended(NumberValue operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand2.addressingMode() == AddressingMode.REGISTER) {
      resolved = resolveOperandExtendedToRegister(operand1, operand2.asRegister());
    }

    return resolved;
  }

  private static ByteSource resolveOperandExtendedToRegister(
      NumberValue value, Register register2) {
    return switch (register2) {
      case Register.A -> ByteSource.of(0x32, value.lsb().value(), value.msb().value());
      case Register.HL -> ByteSource.of(0x22, value.lsb().value(), value.msb().value());
      case Register.IX -> ByteSource.of(0xDD, 0x22, value.lsb().value(), value.msb().value());
      case Register.IY -> ByteSource.of(0xFD, 0x22, value.lsb().value(), value.msb().value());
      default -> ByteSource.of(
          0xED, 0b1000011 | Registers.dd.get(register2), value.lsb().value(), value.msb().value());
    };
  }

  private static ByteSource resolveOperandRegisterToRegisterIndirect(
      Register register1, Register register2) {
    return switch (register1) {
      case Register.HL -> ByteSource.of(0b01110000 | (Registers.r.get(register2)));
      case Register.A -> {
        if (register2 == Register.BC) {
          yield ByteSource.of(0x02);
        } else if (register2 == Register.DE) {
          yield ByteSource.of(0x12);
        }

        yield null;
      }
      default -> null;
    };
  }

  private static ByteSource resolveOperandRegisterIndexedToRegister(
      Register register1, Operand register2) {
    return switch (register2.asRegister()) {
      case Register.IX -> ByteSource.of(
          0xDD, 0b01000110 | Registers.r.get(register1) << 3, register2.displacementD());
      case Register.IY -> ByteSource.of(
          0xFD, 0b01000110 | Registers.r.get(register1) << 3, register2.displacementD());
      default -> null;
    };
  }

  private static ByteSource resolveOperandByteValueToRegisterIndirect(
      Register register, NumberValue value) {
    return ByteSource.of(0x36, value.value());
  }

  private static ByteSource resolveOperandByteValueToRegisterIndexed(
      Operand register, NumberValue value) {
    return switch (register.asRegister()) {
      case Register.IX -> ByteSource.of(0xDD, 0x36, register.displacementD(), value.value());
      case Register.IY -> ByteSource.of(0xFD, 0x36, register.displacementD(), value.value());
      default -> null;
    };
  }

  private static ByteSource resolveOperandRegisterIndirectToRegister(
      Register register1, Register register2) {
    ByteSource resolved = null;

    if (register1 == Register.A && (register2 == Register.BC || register2 == Register.DE)) {
      if (register2 == Register.BC) {
        resolved = ByteSource.of(0x0A);
      } else if (register2 == Register.DE) {
        resolved = ByteSource.of(0x1A);
      }
    } else if (register2 == Register.HL) {
      resolved = ByteSource.of(0b01000110 | (Registers.r.get(register1) << 3));
    }

    return resolved;
  }

  private static ByteSource resolveOperandByteValueToRegister(
      Register operand1, NumberValue value) {
    return ByteSource.of(0b00000110 | (Registers.r.get(operand1) << 3), value.value());
  }

  private static ByteSource resolveOperandWordValueToRegister(
      Register operand1, NumberValue value) {
    return ByteSource.of(
        0b00000001 | (Registers.dd.get(operand1) << 4), value.lsb().value(), value.msb().value());
  }

  private static ByteSource resolveOperandExtendedToRegister(Register register, NumberValue value) {
    return switch (register) {
      case Register.A -> ByteSource.of(0x3A, value.lsb().value(), value.msb().value());
      case Register.HL -> ByteSource.of(0x2A, value.lsb().value(), value.msb().value());
      case Register.IX -> ByteSource.of(0xDD, 0x2A, value.lsb().value(), value.msb().value());
      case Register.IY -> ByteSource.of(0xFD, 0x2A, value.lsb().value(), value.msb().value());
      default -> ByteSource.of(
          0xED,
          0b01001011 | (Registers.dd.get(register) << 4),
          value.lsb().value(),
          value.msb().value());
    };
  }

  private static ByteSource resolveOperandRegisterToRegister(
      Register register1, Register register2) {
    return switch (register1) {
      case Register.A -> {
        if (register2 == Register.I) {
          yield ByteSource.of(0xED, 0x57);
        } else if (register2 == Register.R) {
          yield ByteSource.of(0xED, 0x5F);
        }

        yield null;
      }
      case Register.I -> {
        if (register2 == Register.A) {
          yield ByteSource.of(0xED, 0x47);
        }
        yield null;
      }
      case Register.R -> {
        if (register2 == Register.A) {
          yield ByteSource.of(0xED, 0x4F);
        }
        yield null;
      }
      default -> ByteSource.of(
          0b01000000 | (Registers.r.get(register1) << 3) | (Registers.r.get(register2)));
    };
  }
}
