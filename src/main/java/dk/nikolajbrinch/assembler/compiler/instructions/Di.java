package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.NumberValue;

public class Di implements InstructionGenerator {

  @Override
  public ByteSource generate(NumberValue currentAddress) {
    return ByteSource.of(0xF3);
  }
}
