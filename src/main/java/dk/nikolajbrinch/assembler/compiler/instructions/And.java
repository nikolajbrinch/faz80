package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class And implements InstructionGenerator {

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
