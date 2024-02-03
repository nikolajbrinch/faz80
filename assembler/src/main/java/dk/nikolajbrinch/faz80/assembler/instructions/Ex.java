package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.parser.Register;

public class Ex implements InstructionGenerator {

  @Override
  public ByteSource generateRegisterToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister) {
    return switch (targetRegister) {
      case DE -> switch (sourceRegister) {
        case HL -> ByteSource.of(0xEB);
        default -> null;
      };
      case AF -> switch (sourceRegister) {
        case AF_QUOTE -> ByteSource.of(0x08);
        default -> null;
      };
      default -> null;
    };
  }

  @Override
  public ByteSource generateRegisterToRegisterIndirect(
      Address currentAddress, Register targetRegister, Register sourceRegister) {
    return switch (targetRegister) {
      case SP -> switch (sourceRegister) {
        case HL -> ByteSource.of(0xE3);
        case IX -> ByteSource.of(0xDD, 0xE3);
        case IY -> ByteSource.of(0xFD, 0xE3);
        default -> null;
      };
      default -> null;
    };
  }
}
