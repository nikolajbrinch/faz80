package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.ByteSource;

public class Lddr implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress) {
    return ByteSource.of(0xED, 0xB8);
  }
}
