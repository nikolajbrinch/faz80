package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.parser.operands.Operand;
import dk.nikolajbrinch.faz80.parser.operands.RegisterOperand;
import dk.nikolajbrinch.faz80.parser.statements.InstructionStatement;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import dk.nikolajbrinch.parser.ParseMessage;
import dk.nikolajbrinch.scanner.Line;
import java.util.List;

class InstructionFixer {

  public InstructionStatement fixInstruction(
      Mnemonic instruction,
      AssemblerToken mnemonic,
      List<Operand> operands,
      List<ParseMessage> errors) {
    return switch (instruction) {
      case ADD, ADC, SBC -> fixImplicitRegisterA(instruction, mnemonic, operands, errors);
      case SUB, AND, OR, XOR -> fixExplicitRegisterA(instruction, mnemonic, operands, errors);
      default -> null;
    };
  }

  private InstructionStatement fixImplicitRegisterA(
      Mnemonic instruction,
      AssemblerToken mnemonic,
      List<Operand> operands,
      List<ParseMessage> errors) {
    if (operands.size() == 1) {
      errors.add(
          ParseMessage.warning(
              mnemonic,
              "Instruction "
                  + instruction
                  + " requires at least "
                  + instruction.getOperandsLowerBound()
                  + " operands"));

      return new InstructionStatement(
          mnemonic,
          List.of(
              new RegisterOperand(AssemblerToken.NONE, Line.NONE, Register.A, null),
              operands.get(0)));
    }

    return null;
  }

  private InstructionStatement fixExplicitRegisterA(
      Mnemonic instruction,
      AssemblerToken mnemonic,
      List<Operand> operands,
      List<ParseMessage> errors) {
    if (operands.size() == 2) {
      Operand operand = operands.get(0);

      if (operand instanceof RegisterOperand registerOperand) {
        if (registerOperand.register() == Register.A) {
          errors.add(
              ParseMessage.warning(
                  mnemonic,
                  "Instruction "
                      + instruction
                      + " accepts maximum "
                      + instruction.getOperandsLowerBound()
                      + " operands"));

          return new InstructionStatement(mnemonic, List.of(operands.get(1)));
        }
      }
    }

    return null;
  }
}
