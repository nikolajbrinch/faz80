package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;

public class Exx implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress) {
    return ByteSource.of(0xD9);
  }
}
