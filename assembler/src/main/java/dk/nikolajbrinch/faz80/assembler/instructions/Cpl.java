package dk.nikolajbrinch.faz80.assembler.instructions;

import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.assembler.ByteSource;

public class Cpl implements InstructionGenerator {

  @Override
  public ByteSource generate(Address currentAddress) {
    return ByteSource.of(0x2F);
  }
}
