package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ByteSupplier;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Xor implements InstructionGenerator {

  @Override
  public ByteSource generate(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> ByteSource.of(0b10101000 | Registers.r.get(targetOperand.asRegister()));
      case REGISTER_INDIRECT -> {
        if (targetOperand.asRegister() == Register.HL) {
          yield ByteSource.of(0xAE);
        }

        yield null;
      }
      case IMMEDIATE ->
          ByteSource.of(0xEE, ByteSupplier.of(() -> targetOperand.asValue().number().value()));
      case INDEXED ->
          switch (targetOperand.asRegister()) {
            case IX -> ByteSource.of(0xDD, 0xAE, ByteSupplier.of(targetOperand::displacementD));
            case IY -> ByteSource.of(0xFD, 0xAE, ByteSupplier.of(targetOperand::displacementD));
            default -> null;
          };
      default -> null;
    };
  }
}
