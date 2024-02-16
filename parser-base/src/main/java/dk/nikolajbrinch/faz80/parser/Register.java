package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.values.NumberValue.Size;
import java.util.Arrays;

public enum Register {
  A("A", Size.BYTE),
  B("B", Size.BYTE),
  C("C", Size.BYTE),
  D("D", Size.BYTE),
  E("E", Size.BYTE),
  F("F", Size.BYTE),
  H("H", Size.BYTE),
  L("L", Size.BYTE),
  I("I", Size.BYTE),
  R("R", Size.BYTE),
  IXH("IXH", Size.BYTE),
  IXL("IXL", Size.BYTE),
  IYH("IYH", Size.BYTE),
  IYL("IYL", Size.BYTE),

  AF("AF", Size.WORD),
  BC("BC", Size.WORD),
  DE("DE", Size.WORD),
  HL("HL", Size.WORD),
  PC("PC", Size.WORD),
  SP("SP", Size.WORD),
  IX("IX", Size.WORD),
  IY("IY", Size.WORD),
  AF_QUOTE("AF'", Size.WORD);

  private final String text;

  private final Size size;

  Register(String text, Size size) {
    this.text = text;
    this.size = size;
  }

  public static Register find(final String text) {
    return Arrays.stream(values())
        .filter(register -> register.text.equalsIgnoreCase(text))
        .findAny()
        .orElse(null);
  }

  public Size size() {
    return size;
  }
}
