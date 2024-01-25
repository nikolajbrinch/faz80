package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Cp implements InstructionGenerator {

  @Override
  public ByteSource generateRegister(Address currentAddress, Register register) {
    return ByteSource.of(0b10111000 | Registers.r.get(register));
  }

  @Override
  public ByteSource generateRegisterIndirect(Address currentAddress, Register register) {
    if (register == Register.HL) {
      return ByteSource.of(0xBE);
    }

    return null;
  }

  @Override
  public ByteSource generateImmediate(Address numberValue, ValueSupplier value) {
    return ByteSource.of(0xFE, ByteSupplier.of(() -> value.number().value()));
  }

  @Override
  public ByteSource generateIndexed(
      Address currentAddress, Register register, ByteSupplier displacement) {
    return switch (register) {
      case IX -> ByteSource.of(0xDD, 0xBE, displacement);
      case IY -> ByteSource.of(0xFD, 0xBE, displacement);
      default -> null;
    };
  }
}
