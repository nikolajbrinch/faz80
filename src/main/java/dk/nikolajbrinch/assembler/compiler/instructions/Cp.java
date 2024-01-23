package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Cp implements InstructionGenerator {

  @Override
  public ByteSource generate(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> ByteSource.of(0b10111000 | Registers.r.get(targetOperand.asRegister()));
      case REGISTER_INDIRECT -> {
        if (targetOperand.asRegister() == Register.HL) {
          yield ByteSource.of(0xBE);
        }

        yield null;
      }
      case IMMEDIATE ->
          ByteSource.of(0xFE, ByteSupplier.of(() -> targetOperand.asValue().number().value()));
      default -> null;
    };
  }
}
