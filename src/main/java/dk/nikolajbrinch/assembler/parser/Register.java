package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import java.util.Arrays;
import java.util.Set;

public enum Register {
  A,
  B,
  C,
  D,
  E,
  F,
  H,
  L,
  I,
  R,
  IXH,
  IXL,
  IYH,
  IYL,

  AF,
  BC,
  DE,
  HL,
  PC,
  SP,
  IX,
  IY,
  AF_BANG;

  private static final Set<Register> WORD_REGISTER =
      Set.of(AF, BC, DE, HL, PC, SP, IX, IY, AF_BANG);

  public static Register find(String text) {
    return Arrays.stream(values())
        .filter(register -> register.name().equalsIgnoreCase(text))
        .findAny()
        .orElse(null);
  }

  public Size size() {
    return WORD_REGISTER.contains(this) ? Size.WORD : Size.BYTE;
  }
}
