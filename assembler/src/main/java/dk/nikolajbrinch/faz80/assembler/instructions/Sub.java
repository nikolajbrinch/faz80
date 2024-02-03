package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.Registers;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.Register;

public class Sub implements InstructionGenerator {

  @Override
  public ByteSource generate(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {

    ByteSource resolved = null;

    resolved =
        switch (targetOperand.addressingMode()) {
          case REGISTER -> ByteSource.of(0b10010000 | Registers.r.get(targetOperand.asRegister()));
          case REGISTER_INDIRECT -> {
            if (targetOperand.asRegister() == Register.HL) {
              yield ByteSource.of(0x96);
            }

            yield null;
          }
          case IMMEDIATE ->
              ByteSource.of(0xD6, ByteSupplier.of(() -> targetOperand.asValue().number().value()));
          default -> null;
        };

    return resolved;
  }
}
