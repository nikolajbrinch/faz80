package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.parser.Register;
import java.util.Map;

public interface InstructionGenerator {

  default ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    ByteSource generated;

    if (operand1 == null && operand2 == null) {
      generated = generate(currentAddress);
    } else if (operand2 == null) {
      generated = generateSingleOperand(currentAddress, operand1);
    } else {
      generated = generateTwoOperands(currentAddress, operand1, operand2);
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
      case INDEXED -> generateIndexed(currentAddress, operand);
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

  default ByteSource generateIndexed(NumberValue currentAddress, Operand operand) {
    return null;
  }

  private ByteSource generateTwoOperands(
      NumberValue currentAddress, Operand operand1, Operand operand2) {
    return switch (operand2.addressingMode()) {
      case REGISTER -> generateSourceOperandRegister(
          currentAddress, operand1, operand2.asRegister());
      case REGISTER_INDIRECT -> generateSourceOperandRegisterIndirect(
          currentAddress, operand1, operand2.asRegister());
      case EXTENDED -> generateSourceOperandExtended(
          currentAddress, operand1, operand2.asNumberValue());
      case IMMEDIATE -> generateSourceOperandImmediate(
          currentAddress, operand1, operand2.asNumberValue());
      case IMMEDIATE_EXTENDED -> generateSourceOperandImmediateExtended(
          currentAddress, operand1, operand2.asNumberValue());
      case INDEXED -> generateSourceOperandIndexed(currentAddress, operand1, operand2);
      default -> null;
    };
  }

  private ByteSource generateSourceOperandRegister(
      NumberValue currentAddress, Operand operand1, Register register) {
    return switch (operand1.addressingMode()) {
      case REGISTER -> generateRegisterToRegister(currentAddress, operand1.asRegister(), register);
      case REGISTER_INDIRECT -> generateRegisterToRegisterIndirect(
          currentAddress, operand1, register);
      case EXTENDED -> generateRegisterToExtended(
          currentAddress, operand1.asNumberValue(), register);
      case IMMEDIATE -> generateRegisterToImmediate(
          currentAddress, operand1.asNumberValue(), register);
      case IMMEDIATE_EXTENDED -> generateRegisterToImmediateExtended(
          currentAddress, operand1.asNumberValue(), register);
      case INDEXED -> generateRegisterToIndexed(currentAddress, operand1, register);
      default -> null;
    };
  }

  default ByteSource generateRegisterToRegister(
      NumberValue currentAddress, Register register, Register register1) {
    return null;
  }

  default ByteSource generateRegisterToRegisterIndirect(
      NumberValue currentAddress, Operand operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterToExtended(
      NumberValue currentAddress, NumberValue operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterToImmediate(
      NumberValue currentAddress, NumberValue operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterToImmediateExtended(
      NumberValue currentAddress, NumberValue operand1, Register register) {
    return null;
  }

  default ByteSource generateRegisterToIndexed(
      NumberValue currentAddress, Operand operand1, Register register) {
    return null;
  }

  private ByteSource generateSourceOperandRegisterIndirect(
      NumberValue currentAddress, Operand operand1, Register register) {
    return switch (operand1.addressingMode()) {
      case REGISTER -> generateRegisterIndirectToRegister(
          currentAddress, operand1.asRegister(), register);
      case REGISTER_INDIRECT -> generateRegisterIndirectToRegisterIndirect(
          currentAddress, operand1.asRegister(), register);
      case EXTENDED -> generateRegisterIndirectToExtended(
          currentAddress, operand1.asNumberValue(), register);
      case IMMEDIATE -> generateRegisterIndirectToImmediate(
          currentAddress, operand1.asNumberValue(), register);
      case IMMEDIATE_EXTENDED -> generateRegisterIndirectToImmediateExtended(
          currentAddress, operand1.asNumberValue(), register);
      case INDEXED -> generateRegisterIndirectToIndexed(currentAddress, operand1, register);
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
      NumberValue currentAddress, Operand operand1, Register register) {
    return null;
  }

  private ByteSource generateSourceOperandExtended(
      NumberValue currentAddress, Operand operand1, NumberValue numberValue) {
    return switch (operand1.addressingMode()) {
      case REGISTER -> generateExtendedToRegister(
          currentAddress, operand1.asRegister(), numberValue);
      case REGISTER_INDIRECT -> generateExtendedToRegisterIndirect(
          currentAddress, operand1.asRegister(), numberValue);
      case EXTENDED -> generateExtendedToExtended(
          currentAddress, operand1.asNumberValue(), numberValue);
      case IMMEDIATE -> generateExtendedToImmediate(
          currentAddress, operand1.asNumberValue(), numberValue);
      case IMMEDIATE_EXTENDED -> generateExtendedToImmediateExtended(
          currentAddress, operand1.asNumberValue(), numberValue);
      case INDEXED -> generateExtendedToIndexed(currentAddress, operand1, numberValue);
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
      NumberValue currentAddress, Operand operand1, NumberValue numberValue) {
    return null;
  }

  private ByteSource generateSourceOperandImmediate(
      NumberValue currentAddress, Operand operand1, NumberValue numberValue) {
    return switch (operand1.addressingMode()) {
      case REGISTER -> generateImmediateToRegister(
          currentAddress, operand1.asRegister(), numberValue);
      case REGISTER_INDIRECT -> generateImmediateToRegisterIndirect(
          currentAddress, operand1.asRegister(), numberValue);
      case EXTENDED -> generateImmediateToExtended(
          currentAddress, operand1.asNumberValue(), numberValue);
      case IMMEDIATE -> generateImmediateToImmediate(
          currentAddress, operand1.asNumberValue(), numberValue);
      case IMMEDIATE_EXTENDED -> generateImmediateToImmediateExtended(
          currentAddress, operand1.asNumberValue(), numberValue);
      case INDEXED -> generateImmediateToIndexed(currentAddress, operand1, numberValue);
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
      NumberValue currentAddress, Operand operand1, NumberValue numberValue) {
    return null;
  }

  private ByteSource generateSourceOperandImmediateExtended(
      NumberValue currentAddress, Operand operand1, NumberValue numberValue) {
    return switch (operand1.addressingMode()) {
      case REGISTER -> generateImmediateExtendedToRegister(
          currentAddress, operand1.asRegister(), numberValue);
      case REGISTER_INDIRECT -> generateImmediateExtendedToRegisterIndirect(
          currentAddress, operand1.asRegister(), numberValue);
      case EXTENDED -> generateImmediateExtendedToExtended(
          currentAddress, operand1.asNumberValue(), numberValue);
      case IMMEDIATE -> generateImmediateExtendedToImmediate(
          currentAddress, operand1.asNumberValue(), numberValue);
      case IMMEDIATE_EXTENDED -> generateImmediateExtendedToImmediateExtended(
          currentAddress, operand1.asNumberValue(), numberValue);
      case INDEXED -> generateImmediateExtendedToIndexed(currentAddress, operand1, numberValue);
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
      NumberValue currentAddress, Operand operand1, NumberValue numberValue) {
    return null;
  }

  private ByteSource generateSourceOperandIndexed(
      NumberValue currentAddress, Operand operand1, Operand operand2) {
    return switch (operand1.addressingMode()) {
      case REGISTER -> generateIndexedToRegister(currentAddress, operand1.asRegister(), operand2);
      case REGISTER_INDIRECT -> generateIndexedToRegisterIndirect(
          currentAddress, operand1.asRegister(), operand2);
      case EXTENDED -> generateIndexedToExtended(
          currentAddress, operand1.asNumberValue(), operand2);
      case IMMEDIATE -> generateIndexedToImmediate(
          currentAddress, operand1.asNumberValue(), operand2);
      case IMMEDIATE_EXTENDED -> generateIndexedToImmediateExtended(
          currentAddress, operand1.asNumberValue(), operand2);
      case INDEXED -> generateIndexedToIndexed(currentAddress, operand1, operand2);
      default -> null;
    };
  }

  default ByteSource generateIndexedToRegister(
      NumberValue currentAddress, Register register, Operand operand2) {
    return null;
  }

  default ByteSource generateIndexedToRegisterIndirect(
      NumberValue currentAddress, Register register, Operand operand2) {
    return null;
  }

  default ByteSource generateIndexedToExtended(
      NumberValue currentAddress, NumberValue numberValue, Operand operand2) {
    return null;
  }

  default ByteSource generateIndexedToImmediate(
      NumberValue currentAddress, NumberValue numberValue, Operand operand2) {
    return null;
  }

  default ByteSource generateIndexedToImmediateExtended(
      NumberValue currentAddress, NumberValue numberValue, Operand operand2) {
    return null;
  }

  default ByteSource generateIndexedToIndexed(
      NumberValue currentAddress, Operand operand1, Operand operand2) {
    return null;
  }

  default ByteSource generate(NumberValue currentAddres) {
    return null;
  }

  public static long implied1(int mask, Map<Register, Integer> registerSet, Register register) {
    return mask | registerSet.get(register);
  }

  public static long implied5(int mask, Map<Register, Integer> registerSet, Register register) {
    return mask | (registerSet.get(register) << 4);
  }
}
