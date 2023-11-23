package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue;

public class Djnz implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand targetOperand, Operand sourceOperand) {
    return ByteSource.of(0x20, targetOperand.displacementE(currentAddress).value());
  }
}
