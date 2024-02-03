package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.Register;

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
