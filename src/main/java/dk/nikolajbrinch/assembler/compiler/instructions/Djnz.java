package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;

public class Djnz implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress, Operand targetOperand, Operand sourceOperand) {
    return ByteSource.of(0x20, targetOperand.displacementE(currentAddress).value());
  }
}
