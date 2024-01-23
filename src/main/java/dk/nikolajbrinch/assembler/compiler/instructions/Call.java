package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Conditions;
import dk.nikolajbrinch.assembler.compiler.operands.EvaluatedOperand;
import dk.nikolajbrinch.assembler.parser.Condition;

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
