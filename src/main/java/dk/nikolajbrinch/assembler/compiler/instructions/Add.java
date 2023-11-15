package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.operands.Registers;
import dk.nikolajbrinch.assembler.parser.Register;

public class Add implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource resolved = null;

    if (operand1.operand() instanceof Register register) {
      resolved =
          switch (register) {
            case Register.A -> switch (operand2.addressingMode()) {
              case REGISTER -> ByteSource.of(
                  InstructionGenerator.implied1(0b10000000, Registers.r, operand2.asRegister()));
              case REGISTER_INDIRECT -> {
                if (operand2.asRegister() == Register.HL) {
                  yield ByteSource.of(0x86);
                }

                yield null;
              }
              case IMMEDIATE -> ByteSource.of(0xC6, operand2.asNumberValue().value());
              case INDEXED -> {
                if (operand2.asRegister() == Register.IX) {
                  yield ByteSource.of(0xDD, 0x86, operand2.displacementD());
                } else if (operand2.asRegister() == Register.IY) {
                  yield ByteSource.of(0xFD, 0x86, operand2.displacementD());
                }

                yield null;
              }
              default -> null;
            };
            case Register.HL -> {
              if (operand2.operand() instanceof Register register2) {
                yield ByteSource.of(
                    InstructionGenerator.implied5(0b00001001, Registers.ss, register2));
              }

              yield null;
            }
            case Register.IX -> {
              if (operand2.operand() instanceof Register register2) {
                yield ByteSource.of(
                    0xDD, InstructionGenerator.implied5(0b00001001, Registers.pp, register2));
              }

              yield null;
            }
            case Register.IY -> {
              if (operand2.operand() instanceof Register register2) {
                yield ByteSource.of(
                    0xFD, InstructionGenerator.implied5(0b00001001, Registers.rr, register2));
              }

              yield null;
            }
            default -> null;
          };
    }

    if (resolved == null) {
      throw new IllegalStateException();
    }

    return resolved;
  }
}
