package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;

public class Djnz implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress, Operand operand1, Operand operand2) {
    return ByteSource.of(0x20, operand1.displacementE(currentAddress).value());
  }
}
