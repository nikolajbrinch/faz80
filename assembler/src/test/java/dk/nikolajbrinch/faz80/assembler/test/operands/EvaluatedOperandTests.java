package dk.nikolajbrinch.faz80.assembler.test.operands;

import dk.nikolajbrinch.faz80.assembler.operands.AddressingMode;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.parser.base.values.NumberValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EvaluatedOperandTests {

  @Test
  void testNegative() {
    NumberValue address = NumberValue.create(0x480);
    Address currentAddress = new Address(address, address);

    EvaluatedOperand operand =
        new EvaluatedOperand(NumberValue.create(0x47c), "", false, AddressingMode.REGISTER);

    long displacementE = operand.displacementE(currentAddress);

    Assertions.assertEquals((byte) -6, (byte) displacementE);
  }

  @Test
  void testPositive() {
    NumberValue address = NumberValue.create(0x480);
    Address currentAddress = new Address(address, address);

    EvaluatedOperand operand =
        new EvaluatedOperand(NumberValue.create(0x485), "", false, AddressingMode.REGISTER);

    long displacementE = operand.displacementE(currentAddress);

    Assertions.assertEquals(3, displacementE);
  }
}
