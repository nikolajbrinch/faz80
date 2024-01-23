package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Dec implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(Address currentAddress, Register register) {
    if (Registers.r.containsKey(register)) {
      return ByteSource.of(0b00000101 | (Registers.r.get(register) << 3));
    }

    if (Registers.ss.containsKey(register)) {
      return ByteSource.of(implied5(0b00001011, Registers.ss, register));
    }

    return switch (register) {
      case IX -> ByteSource.of(0xDD, 0x2B);
      case IY -> ByteSource.of(0xFD, 0x2B);
      default -> null;
    };
  }

  @Override
  public ByteSource generateRegisterIndirect(Address currentAddress, Register register) {
    return switch (register) {
      case HL -> ByteSource.of(() -> 0x35);
      default -> null;
    };
  }

  @Override
  public ByteSource generateIndexed(Address currentAddress, Register register, ByteSupplier displacement) {
    return switch (register) {
      case IX -> ByteSource.of(0xDD, 0x35, displacement);
      case IY -> ByteSource.of(0xFD, 0x35, displacement);
      default -> null;
    };
  }
}
