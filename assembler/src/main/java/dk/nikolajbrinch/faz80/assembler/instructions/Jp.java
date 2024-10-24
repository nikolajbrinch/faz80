package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.operands.Conditions;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.parser.base.Condition;
import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;

public class Jp implements InstructionGenerator {

  @Override
  public ByteSource generateImmediate(Address numberValue, ValueSupplier value) {
    return generateImmediateExtended(numberValue, value);
  }

  @Override
  public ByteSource generateImmediateExtended(Address numberValue, ValueSupplier value) {
    return ByteSource.of(0xC3, lsb(value), msb(value));
  }

  @Override
  public ByteSource generateRegisterIndirect(Address currentAddress, Register register) {
    return switch (register) {
      case HL -> ByteSource.of(0xE9);
      case IX -> ByteSource.of(0xDD, 0xE9);
      case IY -> ByteSource.of(0xFD, 0xE9);
      default -> null;
    };
  }

  @Override
  public ByteSource generateConditionImmediate(
      Address currentAddress,
      Condition condition,
      ValueSupplier value,
      EvaluatedOperand displacement) {
    return generateConditionImmediateExtended(currentAddress, condition, value, displacement);
  }

  @Override
  public ByteSource generateConditionImmediateExtended(
      Address currentAddress,
      Condition condition,
      ValueSupplier value,
      EvaluatedOperand displacement) {
    return ByteSource.of(0b11000010 | Conditions.cc.get(condition), lsb(value), msb(value));
  }
}
