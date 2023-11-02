package dk.nikolajbrinch.assembler.parser;

import java.util.Arrays;

public enum Register {
  A, B, C, D, E, F, H, L, I, R, IXH, IXL, IYH, IYL,

  AF, BC, DE, HL, PC, SP, IX, IY;

  public static Register find(String text) {
    return Arrays.stream(values()).filter(register -> register.name().equalsIgnoreCase(text)).findAny().orElse(null);
  }
}
