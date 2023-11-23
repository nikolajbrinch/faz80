package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.Register;
import java.util.Map;

public interface InstructionGenerator {

  static long implied1(int mask, Map<Register, Integer> registerSet, Register register) {
    return mask | registerSet.get(register);
  }

  static long implied5(int mask, Map<Register, Integer> registerSet, Register register) {
    return mask | (registerSet.get(register) << 4);
  }

  default ByteSource generate(
      NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    ByteSource generated;

    if (targetOperand == null && sourceOperand == null) {
      generated = generate(currentAddress);
    } else if (sourceOperand == null) {
      generated = generateSingleOperand(currentAddress, targetOperand);
    } else {
      generated = generateTwoOperands(currentAddress, targetOperand, sourceOperand);
    }
    if (generated == null) {
      throw new IllegalStateException();
    }

    return generated;
  }

  private ByteSource generateSingleOperand(NumberValue currentAddress, Operand operand) {
    return switch (operand.addressingMode()) {
      case REGISTER -> generateRegister(currentAddress, operand.asRegister());
      case REGISTER_INDIRECT -> generateRegisterIndirect(currentAddress, operand.asRegister());
      case EXTENDED -> generateExtended(currentAddress, operand.asNumberValue());
      case IMMEDIATE -> generateImmediate(currentAddress, operand.asNumberValue());
      case IMMEDIATE_EXTENDED -> generateImmediateExtended(currentAddress, operand.asNumberValue());
      case INDEXED -> generateIndexed(
          currentAddress, operand.asRegister(), operand.displacementD());
      default -> null;
    };
  }

  default ByteSource generateRegister(NumberValue currentAddress, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirect(NumberValue currentAddress, Register register) {
    return null;
  }

  default ByteSource generateExtended(NumberValue currentAddress, NumberValue operand) {
    return null;
  }

  default ByteSource generateImmediate(NumberValue numberValue, NumberValue value) {
    return null;
  }

  default ByteSource generateImmediateExtended(NumberValue numberValue, NumberValue value) {
    return null;
  }

  default ByteSource generateIndexed(
      NumberValue currentAddress, Register register, long displacement) {
    return null;
  }

  private ByteSource generateTwoOperands(
      NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    return switch (sourceOperand.addressingMode()) {
      case REGISTER -> generateSourceOperandRegister(
          currentAddress, targetOperand, sourceOperand.asRegister());
      case REGISTER_INDIRECT -> generateSourceOperandRegisterIndirect(
          currentAddress, targetOperand, sourceOperand.asRegister());
      case EXTENDED -> generateSourceOperandExtended(
          currentAddress, targetOperand, sourceOperand.asNumberValue());
      case IMMEDIATE -> generateSourceOperandImmediate(
          currentAddress, targetOperand, sourceOperand.asNumberValue());
      case IMMEDIATE_EXTENDED -> generateSourceOperandImmediateExtended(
          currentAddress, targetOperand, sourceOperand.asNumberValue());
      case INDEXED -> generateSourceOperandIndexed(
          currentAddress, targetOperand, sourceOperand.asRegister(), sourceOperand.displacementD());
      default -> null;
    };
  }

  private ByteSource generateSourceOperandRegister(
      NumberValue currentAddress, Operand targetOperand, Register sourceRegister) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> generateRegisterToRegister(
          currentAddress, targetOperand.asRegister(), sourceRegister);
      case REGISTER_INDIRECT -> generateRegisterToRegisterIndirect(
          currentAddress, targetOperand.asRegister(), sourceRegister);
      case EXTENDED -> generateRegisterToExtended(
          currentAddress, targetOperand.asNumberValue(), sourceRegister);
      case IMMEDIATE -> generateRegisterToImmediate(
          currentAddress, targetOperand.asNumberValue(), sourceRegister);
      case IMMEDIATE_EXTENDED -> generateRegisterToImmediateExtended(
          currentAddress, targetOperand.asNumberValue(), sourceRegister);
      case INDEXED -> generateRegisterToIndexed(
          currentAddress,
          targetOperand.asRegister(),
          targetOperand.displacementD(),
          sourceRegister);
      default -> null;
    };
  }

  default ByteSource generateRegisterToRegister(
      NumberValue currentAddress, Register targetRegister, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToRegisterIndirect(
      NumberValue currentAddress, Register targetRegister, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToExtended(
      NumberValue currentAddress, NumberValue numberValue, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToImmediate(
      NumberValue currentAddress, NumberValue numberValue, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToImmediateExtended(
      NumberValue currentAddress, NumberValue numberValue, Register sourceRegister) {
    return null;
  }

  default ByteSource generateRegisterToIndexed(
      NumberValue currentAddress,
      Register targetRegister,
      long displacement,
      Register sourceRegister) {
    return null;
  }

  private ByteSource generateSourceOperandRegisterIndirect(
      NumberValue currentAddress, Operand targetOperand, Register sourceRegister) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> generateRegisterIndirectToRegister(
          currentAddress, targetOperand.asRegister(), sourceRegister);
      case REGISTER_INDIRECT -> generateRegisterIndirectToRegisterIndirect(
          currentAddress, targetOperand.asRegister(), sourceRegister);
      case EXTENDED -> generateRegisterIndirectToExtended(
          currentAddress, targetOperand.asNumberValue(), sourceRegister);
      case IMMEDIATE -> generateRegisterIndirectToImmediate(
          currentAddress, targetOperand.asNumberValue(), sourceRegister);
      case IMMEDIATE_EXTENDED -> generateRegisterIndirectToImmediateExtended(
          currentAddress, targetOperand.asNumberValue(), sourceRegister);
      case INDEXED -> generateRegisterIndirectToIndexed(
          currentAddress,
          targetOperand.asRegister(),
          targetOperand.displacementD(),
          sourceRegister);
      default -> null;
    };
  }

  default ByteSource generateRegisterIndirectToRegister(
      NumberValue currentAddress, Register register, Register register1) {
    return null;
  }

  default ByteSource generateRegisterIndirectToRegisterIndirect(
      NumberValue currentAddress, Register operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirectToExtended(
      NumberValue currentAddress, NumberValue operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirectToImmediate(
      NumberValue currentAddress, NumberValue operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirectToImmediateExtended(
      NumberValue currentAddress, NumberValue operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterIndirectToIndexed(
      NumberValue currentAddress,
      Register targetRegister,
      long displacement,
      Register sourceRegister) {
    return null;
  }

  private ByteSource generateSourceOperandExtended(
      NumberValue currentAddress, Operand sourceOperand, NumberValue numberValue) {
    return switch (sourceOperand.addressingMode()) {
      case REGISTER -> generateExtendedToRegister(
          currentAddress, sourceOperand.asRegister(), numberValue);
      case REGISTER_INDIRECT -> generateExtendedToRegisterIndirect(
          currentAddress, sourceOperand.asRegister(), numberValue);
      case EXTENDED -> generateExtendedToExtended(
          currentAddress, sourceOperand.asNumberValue(), numberValue);
      case IMMEDIATE -> generateExtendedToImmediate(
          currentAddress, sourceOperand.asNumberValue(), numberValue);
      case IMMEDIATE_EXTENDED -> generateExtendedToImmediateExtended(
          currentAddress, sourceOperand.asNumberValue(), numberValue);
      case INDEXED -> generateExtendedToIndexed(
          currentAddress, sourceOperand.asRegister(), sourceOperand.displacementD(), numberValue);
      default -> null;
    };
  }

  default ByteSource generateExtendedToRegister(
      NumberValue currentAddress, Register register, NumberValue numberValue) {
    return null;
  }

  default ByteSource generateExtendedToRegisterIndirect(
      NumberValue currentAddress, Register register, NumberValue numberValue) {
    return null;
  }

  default ByteSource generateExtendedToExtended(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateExtendedToImmediate(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateExtendedToImmediateExtended(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateExtendedToIndexed(
      NumberValue currentAddress,
      Register targetRegister,
      long displacement,
      NumberValue numberValue) {
    return null;
  }

  private ByteSource generateSourceOperandImmediate(
      NumberValue currentAddress, Operand targetOperand, NumberValue numberValue) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> generateImmediateToRegister(
          currentAddress, targetOperand.asRegister(), numberValue);
      case REGISTER_INDIRECT -> generateImmediateToRegisterIndirect(
          currentAddress, targetOperand.asRegister(), numberValue);
      case EXTENDED -> generateImmediateToExtended(
          currentAddress, targetOperand.asNumberValue(), numberValue);
      case IMMEDIATE -> generateImmediateToImmediate(
          currentAddress, targetOperand.asNumberValue(), numberValue);
      case IMMEDIATE_EXTENDED -> generateImmediateToImmediateExtended(
          currentAddress, targetOperand.asNumberValue(), numberValue);
      case INDEXED -> generateImmediateToIndexed(
          currentAddress, targetOperand.asRegister(), targetOperand.displacementD(), numberValue);
      default -> null;
    };
  }

  default ByteSource generateImmediateToRegister(
      NumberValue currentAddress, Register register, NumberValue numberValue) {
    return null;
  }

  default ByteSource generateImmediateToRegisterIndirect(
      NumberValue currentAddress, Register register, NumberValue numberValue) {
    return null;
  }

  default ByteSource generateImmediateToExtended(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateImmediateToImmediate(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateImmediateToImmediateExtended(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateImmediateToIndexed(
      NumberValue currentAddress, Register operand1, long displacement, NumberValue numberValue) {
    return null;
  }

  private ByteSource generateSourceOperandImmediateExtended(
      NumberValue currentAddress, Operand targetOperand, NumberValue numberValue) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> generateImmediateExtendedToRegister(
          currentAddress, targetOperand.asRegister(), numberValue);
      case REGISTER_INDIRECT -> generateImmediateExtendedToRegisterIndirect(
          currentAddress, targetOperand.asRegister(), numberValue);
      case EXTENDED -> generateImmediateExtendedToExtended(
          currentAddress, targetOperand.asNumberValue(), numberValue);
      case IMMEDIATE -> generateImmediateExtendedToImmediate(
          currentAddress, targetOperand.asNumberValue(), numberValue);
      case IMMEDIATE_EXTENDED -> generateImmediateExtendedToImmediateExtended(
          currentAddress, targetOperand.asNumberValue(), numberValue);
      case INDEXED -> generateImmediateExtendedToIndexed(
          currentAddress, targetOperand.asRegister(), targetOperand.displacementD(), numberValue);
      default -> null;
    };
  }

  default ByteSource generateImmediateExtendedToRegister(
      NumberValue currentAddress, Register register, NumberValue numberValue) {
    return null;
  }

  default ByteSource generateImmediateExtendedToRegisterIndirect(
      NumberValue currentAddress, Register register, NumberValue numberValue) {
    return null;
  }

  default ByteSource generateImmediateExtendedToExtended(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateImmediateExtendedToImmediate(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateImmediateExtendedToImmediateExtended(
      NumberValue currentAddress, NumberValue numberValue, NumberValue numberValue1) {
    return null;
  }

  default ByteSource generateImmediateExtendedToIndexed(
      NumberValue currentAddress, Register operand1, long displacement, NumberValue numberValue) {
    return null;
  }

  private ByteSource generateSourceOperandIndexed(
      NumberValue currentAddress,
      Operand targetOperand,
      Register sourceRegister,
      long displacement) {
    return switch (targetOperand.addressingMode()) {
      case REGISTER -> generateIndexedToRegister(
          currentAddress, targetOperand.asRegister(), sourceRegister, displacement);
      case REGISTER_INDIRECT -> generateIndexedToRegisterIndirect(
          currentAddress, targetOperand.asRegister(), sourceRegister, displacement);
      case EXTENDED -> generateIndexedToExtended(
          currentAddress, targetOperand.asNumberValue(), sourceRegister, displacement);
      case IMMEDIATE -> generateIndexedToImmediate(
          currentAddress, targetOperand.asNumberValue(), sourceRegister, displacement);
      case IMMEDIATE_EXTENDED -> generateIndexedToImmediateExtended(
          currentAddress, targetOperand.asNumberValue(), sourceRegister, displacement);
      case INDEXED -> generateIndexedToIndexed(
          currentAddress, targetOperand, sourceRegister, displacement);
      default -> null;
    };
  }

  default ByteSource generateIndexedToRegister(
      NumberValue currentAddress,
      Register targetRegister,
      Register sourceRegister,
      long displacement) {
    return null;
  }

  default ByteSource generateIndexedToRegisterIndirect(
      NumberValue currentAddress, Register register, Register operand2, long displacement) {
    return null;
  }

  default ByteSource generateIndexedToExtended(
      NumberValue currentAddress, NumberValue numberValue, Register operand2, long displacement) {
    return null;
  }

  default ByteSource generateIndexedToImmediate(
      NumberValue currentAddress, NumberValue numberValue, Register operand2, long displacement) {
    return null;
  }

  default ByteSource generateIndexedToImmediateExtended(
      NumberValue currentAddress, NumberValue numberValue, Register operand2, long displacement) {
    return null;
  }

  default ByteSource generateIndexedToIndexed(
      NumberValue currentAddress, Operand operand1, Register operand2, long displacement) {
    return null;
  }

  default ByteSource generate(NumberValue currentAddress) {
    return null;
  }
}
