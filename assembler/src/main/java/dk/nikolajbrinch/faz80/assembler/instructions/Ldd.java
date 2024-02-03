package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;

public class Ldd implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress) {
    return ByteSource.of(0xED, 0xA8);
  }
}
