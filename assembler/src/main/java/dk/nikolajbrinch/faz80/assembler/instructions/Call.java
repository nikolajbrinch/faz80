package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.operands.Conditions;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.Condition;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;

public class Call implements InstructionGenerator {

  @Override
  public ByteSource generateImmediate(Address currentAddress, ValueSupplier value) {
    return generateImmediateExtended(currentAddress, value);
  }

  @Override
  public ByteSource generateImmediateExtended(Address currentAddress, ValueSupplier value) {
    return ByteSource.of(0xCD, lsb(value), msb(value));
  }

  @Override
  public ByteSource generateConditionImmediate(
      Address currentAddress, Condition condition, ValueSupplier value, EvaluatedOperand displacement) {
    return generateConditionImmediateExtended(currentAddress, condition, value, displacement);
  }

  @Override
  public ByteSource generateConditionImmediateExtended(
      Address currentAddress, Condition condition, ValueSupplier value, EvaluatedOperand displacement) {
    return ByteSource.of(0b11000100 | (Conditions.cc.get(condition) << 3), lsb(value), msb(value));
  }
}
