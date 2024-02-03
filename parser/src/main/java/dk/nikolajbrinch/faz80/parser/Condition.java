package dk.nikolajbrinch.faz80.parser;

import java.util.Arrays;

public enum Condition {
  NZ,
  Z,
  NC,
  C,
  PO,
  PE,
  P,
  M;

  public static Condition find(String text) {
    return Arrays.stream(values())
        .filter(condition -> condition.name().equalsIgnoreCase(text))
        .findAny()
        .orElse(null);
  }
}
