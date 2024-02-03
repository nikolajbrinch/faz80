package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.assembler.ByteSource;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;

public class Ccf implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress) {
    return ByteSource.of(0x3f);
  }
}
