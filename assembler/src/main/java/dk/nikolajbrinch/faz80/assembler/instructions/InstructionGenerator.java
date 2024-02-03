package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.assembler.ByteSupplier;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.parser.evaluator.ValueSupplier;
import dk.nikolajbrinch.faz80.parser.values.NumberValue.Size;
import dk.nikolajbrinch.faz80.parser.Condition;
import dk.nikolajbrinch.faz80.parser.Register;
import java.util.List;
import java.util.Map;

public interface InstructionGenerator {

  default ByteSource generate(Address currentAddress, List<EvaluatedOperand> operands) {
    return generate(
        currentAddress,
        operands.size() > 0 ? operands.get(0) : null,
        operands.size() > 1 ? operands.get(1) : null,
        operands.size() > 2 ? operands.get(2) : null);
  }

  default ByteSource generate(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {

    ByteSource generated;

    if (targetOperand == null && sourceOperand == null) {
      generated = generate(currentAddress);
    } else if (sourceOperand == null) {
      generated = generateSingleOperand(currentAddress, targetOperand);
    } else if (extraOperand == null) {
      generated = generateTwoOperands(currentAddress, targetOperand, sourceOperand);
    } else {
      generated = generateThreeOperands(currentAddress, extraOperand, sourceOperand, targetOperand);
    }

    if (generated == null) {
      throw new IllegalInstructionException("Illegal instruction");
    }

    return generated;
  }

  private ByteSource generateSingleOperand(Address currentAddress, EvaluatedOperand operand) {
    return switch (operand.addressingMode()) {
      case REGISTER -> generateRegister(currentAddress, operand.asRegister());
      case REGISTER_INDIRECT -> generateRegisterIndirect(currentAddress, operand.asRegister());
      case EXTENDED -> generateExtended(currentAddress, operand.asValue());
      case IMMEDIATE -> generateImmediate(currentAddress, operand.asValue());
      case IMMEDIATE_EXTENDED -> generateImmediateExtended(currentAddress, operand.asValue());
      case INDEXED ->
          generateIndexed(currentAddress, operand.asRegister(), ByteSupplier.of(operand::displacementD));
      case null, default -> null;
    };
  }

  default ByteSource generateRegister(Address currentAddress, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirect(Address currentAddress, Register register) {
    return null;
  }

  default ByteSource generateExtended(Address currentAddress, ValueSupplier value) {
    return null;
  }

  default ByteSource generateImmediate(Address numberValue, ValueSupplier value) {
    return null;
  }

  default ByteSource generateImmediateExtended(Address numberValue, ValueSupplier value) {
    return null;
  }

  default ByteSource generateIndexed(Address currentAddress, Register register, ByteSupplier displacement) {
    return null;
  }

  private ByteSource generateTwoOperands(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand) {
    return switch (sourceOperand.addressingMode()) {
      case REGISTER ->
          generateSourceOperandRegister(currentAddress, targetOperand, sourceOperand.asRegister());
      case REGISTER_INDIRECT ->
          generateSourceOperandRegisterIndirect(
              currentAddress, targetOperand, sourceOperand.asRegister());
      case EXTENDED ->
          generateSourceOperandExtended(currentAddress, targetOperand, sourceOperand.asValue());
      case IMMEDIATE ->
          generateSourceOperandImmediate(currentAddress, targetOperand, sourceOperand);
      case IMMEDIATE_EXTENDED ->
          generateSourceOperandImmediateExtended(currentAddress, targetOperand, sourceOperand);
      case INDEXED ->
          generateSourceOperandIndexed(
              currentAddress,
              targetOperand,
              sourceOperand.asRegister(),
              ByteSupplier.of(sourceOperand::displacementD));
      case null, default -> null;
    };
  }

  private ByteSource generateSourceOperandRegister(
      Address currentAddress, EvaluatedOperand targetOperand, Register sourceRegister) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER ->
          generateRegisterToRegister(currentAddress, targetOperand.asRegister(), sourceRegister);
      case REGISTER_INDIRECT ->
          generateRegisterToRegisterIndirect(
              currentAddress, targetOperand.asRegister(), sourceRegister);
      case EXTENDED ->
          generateRegisterToExtended(currentAddress, targetOperand.asValue(), sourceRegister);
      case IMMEDIATE ->
          generateRegisterToImmediate(currentAddress, targetOperand.asValue(), sourceRegister);
      case IMMEDIATE_EXTENDED ->
          generateRegisterToImmediateExtended(
              currentAddress, targetOperand.asValue(), sourceRegister);
      case INDEXED ->
          generateRegisterToIndexed(
              currentAddress,
              targetOperand.asRegister(),
              ByteSupplier.of(targetOperand::displacementD),
              sourceRegister);
      case null, default -> null;
    };
  }

  default ByteSource generateRegisterToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToRegisterIndirect(
      Address currentAddress, Register targetRegister, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToExtended(
      Address currentAddress, ValueSupplier value, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToImmediate(
      Address currentAddress, ValueSupplier value, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToImmediateExtended(
      Address currentAddress, ValueSupplier value, Register sourceRegister) {
    throw new IllegalInstructionException("Illegal instruction: Register to Immediate Extended");
  }

  default ByteSource generateRegisterToIndexed(
      Address currentAddress, Register targetRegister, ByteSupplier displacement, Register sourceRegister) {
    return null;
  }

  private ByteSource generateSourceOperandRegisterIndirect(
      Address currentAddress, EvaluatedOperand targetOperand, Register sourceRegister) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER ->
          generateRegisterIndirectToRegister(
              currentAddress, targetOperand.asRegister(), sourceRegister);
      case REGISTER_INDIRECT ->
          generateRegisterIndirectToRegisterIndirect(
              currentAddress, targetOperand.asRegister(), sourceRegister);
      case EXTENDED ->
          generateRegisterIndirectToExtended(
              currentAddress, targetOperand.asValue(), sourceRegister);
      case IMMEDIATE ->
          generateRegisterIndirectToImmediate(
              currentAddress, targetOperand.asValue(), sourceRegister);
      case IMMEDIATE_EXTENDED ->
          generateRegisterIndirectToImmediateExtended(
              currentAddress, targetOperand.asValue(), sourceRegister);
      case INDEXED ->
          generateRegisterIndirectToIndexed(
              currentAddress,
              targetOperand.asRegister(),
              ByteSupplier.of(targetOperand::displacementD),
              sourceRegister);
      case null, default -> null;
    };
  }

  default ByteSource generateRegisterIndirectToRegister(
      Address currentAddress, Register register, Register register1) {
    return null;
  }

  default ByteSource generateRegisterIndirectToRegisterIndirect(
      Address currentAddress, Register operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirectToExtended(
      Address currentAddress, ValueSupplier value, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirectToImmediate(
      Address currentAddress, ValueSupplier value, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirectToImmediateExtended(
      Address currentAddress, ValueSupplier value, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirectToIndexed(
      Address currentAddress, Register targetRegister, ByteSupplier displacement, Register sourceRegister) {
    return null;
  }

  private ByteSource generateSourceOperandExtended(
      Address currentAddress, EvaluatedOperand sourceOperand, ValueSupplier value) {
    return switch (sourceOperand.addressingMode()) {
      case REGISTER ->
          generateExtendedToRegister(currentAddress, sourceOperand.asRegister(), value);
      case REGISTER_INDIRECT ->
          generateExtendedToRegisterIndirect(currentAddress, sourceOperand.asRegister(), value);
      case EXTENDED -> generateExtendedToExtended(currentAddress, sourceOperand.asValue(), value);
      case IMMEDIATE -> generateExtendedToImmediate(currentAddress, sourceOperand.asValue(), value);
      case IMMEDIATE_EXTENDED ->
          generateExtendedToImmediateExtended(currentAddress, sourceOperand.asValue(), value);
      case INDEXED ->
          generateExtendedToIndexed(
              currentAddress, sourceOperand.asRegister(), ByteSupplier.of(sourceOperand::displacementD), value);
      case null, default -> null;
    };
  }

  default ByteSource generateExtendedToRegister(
      Address currentAddress, Register register, ValueSupplier value) {
    return null;
  }

  default ByteSource generateExtendedToRegisterIndirect(
      Address currentAddress, Register register, ValueSupplier value) {
    return null;
  }

  default ByteSource generateExtendedToExtended(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateExtendedToImmediate(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateExtendedToImmediateExtended(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateExtendedToIndexed(
      Address currentAddress, Register targetRegister, ByteSupplier displacement, ValueSupplier value) {
    return null;
  }

  private ByteSource generateSourceOperandImmediate(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER ->
          targetOperand.asRegister().size() == Size.BYTE ?
          generateImmediateToRegister(
              currentAddress, targetOperand.asRegister(), sourceOperand.asValue())
          : generateImmediateExtendedToRegister(
              currentAddress, targetOperand.asRegister(), sourceOperand.asValue());
      case REGISTER_INDIRECT ->
          generateImmediateToRegisterIndirect(
              currentAddress, targetOperand.asRegister(), sourceOperand.asValue());
      case EXTENDED ->
          generateImmediateToExtended(
              currentAddress, targetOperand.asValue(), sourceOperand.asValue());
      case IMMEDIATE ->
          generateImmediateToImmediate(
              currentAddress, targetOperand.asValue(), sourceOperand.asValue());
      case IMMEDIATE_EXTENDED ->
          generateImmediateToImmediateExtended(
              currentAddress, targetOperand.asValue(), sourceOperand.asValue());
      case INDEXED ->
          generateImmediateToIndexed(
              currentAddress,
              targetOperand.asRegister(),
              ByteSupplier.of(targetOperand::displacementD),
              sourceOperand.asValue());
      case null, default -> {
        if (targetOperand.operand() instanceof Condition condition) {
          yield generateConditionImmediate(currentAddress, condition, sourceOperand.asValue(), sourceOperand);
        }

        yield null;
      }
    };
  }

  default ByteSource generateImmediateToRegister(
      Address currentAddress, Register register, ValueSupplier value) {
    return null;
  }

  default ByteSource generateImmediateToRegisterIndirect(
      Address currentAddress, Register register, ValueSupplier value) {
    return null;
  }

  default ByteSource generateImmediateToExtended(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateImmediateToImmediate(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateImmediateToImmediateExtended(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateImmediateToIndexed(
      Address currentAddress, Register operand1, ByteSupplier displacement, ValueSupplier value) {
    return null;
  }

  default ByteSource generateConditionImmediate(
      Address currentAddress, Condition condition, ValueSupplier value, EvaluatedOperand displacement) {
    return null;
  }

  private ByteSource generateSourceOperandImmediateExtended(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER ->
          generateImmediateExtendedToRegister(
              currentAddress, targetOperand.asRegister(), sourceOperand.asValue());
      case REGISTER_INDIRECT ->
          generateImmediateExtendedToRegisterIndirect(
              currentAddress, targetOperand.asRegister(), sourceOperand.asValue());
      case EXTENDED ->
          generateImmediateExtendedToExtended(
              currentAddress, targetOperand.asValue(), sourceOperand.asValue());
      case IMMEDIATE ->
          generateImmediateExtendedToImmediate(
              currentAddress, targetOperand.asValue(), sourceOperand.asValue());
      case IMMEDIATE_EXTENDED ->
          generateImmediateExtendedToImmediateExtended(
              currentAddress, targetOperand.asValue(), sourceOperand.asValue());
      case INDEXED ->
          generateImmediateExtendedToIndexed(
              currentAddress,
              targetOperand.asRegister(),
              ByteSupplier.of(targetOperand::displacementD),
              sourceOperand.asValue());
      case null, default -> {
        if (targetOperand.operand() instanceof Condition condition) {
          yield generateConditionImmediateExtended(
              currentAddress,
              condition,
              sourceOperand.asValue(),
              sourceOperand);
        }

        yield null;
      }
    };
  }

  default ByteSource generateImmediateExtendedToRegister(
      Address currentAddress, Register register, ValueSupplier value) {
    return null;
  }

  default ByteSource generateImmediateExtendedToRegisterIndirect(
      Address currentAddress, Register register, ValueSupplier value) {
    return null;
  }

  default ByteSource generateImmediateExtendedToExtended(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateImmediateExtendedToImmediate(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateImmediateExtendedToImmediateExtended(
      Address currentAddress, ValueSupplier value, ValueSupplier value1) {
    return null;
  }

  default ByteSource generateImmediateExtendedToIndexed(
      Address currentAddress, Register operand1, ByteSupplier displacement, ValueSupplier value) {
    return null;
  }

  default ByteSource generateConditionImmediateExtended(
      Address currentAddress, Condition condition, ValueSupplier value, EvaluatedOperand displacement) {
    return null;
  }

  private ByteSource generateSourceOperandIndexed(
      Address currentAddress, EvaluatedOperand targetOperand, Register sourceRegister, ByteSupplier displacement) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER ->
          generateIndexedToRegister(
              currentAddress, targetOperand.asRegister(), sourceRegister, displacement);
      case REGISTER_INDIRECT ->
          generateIndexedToRegisterIndirect(
              currentAddress, targetOperand.asRegister(), sourceRegister, displacement);
      case EXTENDED ->
          generateIndexedToExtended(
              currentAddress, targetOperand.asValue(), sourceRegister, displacement);
      case IMMEDIATE ->
          generateIndexedToImmediate(
              currentAddress, targetOperand.asValue(), sourceRegister, displacement);
      case IMMEDIATE_EXTENDED ->
          generateIndexedToImmediateExtended(
              currentAddress, targetOperand.asValue(), sourceRegister, displacement);
      case INDEXED ->
          generateIndexedToIndexed(currentAddress, targetOperand, sourceRegister, displacement);
      case null, default -> null;
    };
  }

  default ByteSource generateIndexedToRegister(
      Address currentAddress, Register targetRegister, Register sourceRegister, ByteSupplier displacement) {
    return null;
  }

  default ByteSource generateIndexedToRegisterIndirect(
      Address currentAddress, Register targetRegister, Register sourceRegister, ByteSupplier displacement) {
    return null;
  }

  default ByteSource generateIndexedToExtended(
      Address currentAddress, ValueSupplier value, Register sourceRegister, ByteSupplier displacement) {
    return null;
  }

  default ByteSource generateIndexedToImmediate(
      Address currentAddress, ValueSupplier value, Register operand2, ByteSupplier displacement) {
    return null;
  }

  default ByteSource generateIndexedToImmediateExtended(
      Address currentAddress, ValueSupplier value, Register operand2, ByteSupplier displacement) {
    return null;
  }

  default ByteSource generateIndexedToIndexed(
      Address currentAddress, EvaluatedOperand targetRegister, Register sourceRegister, ByteSupplier displacement) {
    return null;
  }

  default ByteSource generateThreeOperands(
      Address currentAddress, EvaluatedOperand targetOperand, EvaluatedOperand sourceOperand, EvaluatedOperand extraOperand) {
    return switch (sourceOperand.addressingMode()) {
      case IMMEDIATE ->
          generateSourceOperandImmediate(
              currentAddress, targetOperand, sourceOperand.asValue(), extraOperand);
      case null, default -> null;
    };
  }

  default ByteSource generateSourceOperandImmediate(
      Address currentAddress, EvaluatedOperand targetOperand, ValueSupplier value, EvaluatedOperand extraOperand) {
    return switch (targetOperand.addressingMode()) {
      case INDEXED ->
          generateImmediateToIndexed(
              currentAddress,
              targetOperand.asRegister(),
              value,
              ByteSupplier.of(targetOperand::displacementD),
              extraOperand);
      case null, default -> null;
    };
  }

  default ByteSource generateImmediateToIndexed(
      Address currentAddress,
      Register register,
      ValueSupplier value,
      ByteSupplier displacement,
      EvaluatedOperand extraOperand) {
    return switch (extraOperand.addressingMode()) {
      case REGISTER ->
          generateImmediateToIndexedCopyRegister(
              currentAddress, register, value, displacement, extraOperand.asRegister());
      case null, default -> null;
    };
  }

  default ByteSource generateImmediateToIndexedCopyRegister(
      Address currentAddress,
      Register register,
      ValueSupplier value,
      ByteSupplier displacement,
      Register copyRegister) {
    return null;
  }

  default ByteSource generate(Address currentAddress) {
    return null;
  }

  default ByteSupplier lsb(ValueSupplier value) {
    return ByteSupplier.of(() -> value.number().lsb().value());
  }

  default ByteSupplier msb(ValueSupplier value) {
    return ByteSupplier.of(() -> value.number().msb().value());
  }

  default ByteSupplier val(ValueSupplier value) {
    return ByteSupplier.of(() -> value.number().value());
  }

  default long implied1(int mask, Map<Register, Integer> registerSet, Register register) {
    return mask | registerSet.get(register);
  }

  default long implied5(int mask, Map<Register, Integer> registerSet, Register register) {
    return mask | (registerSet.get(register) << 4);
  }

}
