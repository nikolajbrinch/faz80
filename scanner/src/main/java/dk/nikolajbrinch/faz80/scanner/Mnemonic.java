package dk.nikolajbrinch.faz80.scanner;

import java.util.Arrays;

public enum Mnemonic {
  ADC("ADC",  2),
  ADD("ADD", 2),
  AND("AND", 1, 2),
  BIT("BTI", 2),
  CALL("CALL", 1, 2),
  CCF("CCF", 0),
  CP("CP", 1),
  CPD("CPD", 0),
  CPDR("CPDR", 0),
  CPI("CPI", 0),
  CPIR("CPIR", 0),
  CPL("CPL", 0),
  DAA("DAA", 0),
  DEC("DEC", 1),
  DI("DI", 0),
  DJNZ("DJNZ", 1),
  EI("EI", 0),
  EX("EX", 2),
  EXX("EXX", 0),
  HALT("HALT", 0),
  IM("IM", 1),
  IN("IN", 1, 2),
  INC("INC", 1),
  IND("IND", 0),
  INDR("INDR", 0),
  INI("INI", 0),
  INIR("INIR", 0),
  JP("JP", 1, 2),
  JR("JR", 1, 2),
  LD("LD", 2),
  LDD("LDD", 0),
  LDDR("LDDR", 0),
  LDI("LDI", 0),
  LDIR("LDIR", 0),
  NEG("NEG", 0),
  NOP("NOP", 0),
  OR("OR", 1),
  OTDR("OTDR", 0),
  OTIR("OTIR", 0),

  OUT("OUT", 2),
  OUTD("OUTD", 0),
  OUTI("OUTI", 0),
  POP("POP", 1),
  PUSH("PUSH", 1),
  RES("RES", 2, 3),
  RET("RET", 0, 1),
  RETI("RETI", 0),
  RETN("RETN", 0),
  RL("RL", 1),
  RLA("RLA", 0),
  RLC("RLC", 1),
  RLCA("RLCA", 0),
  RLD("RLD", 0),
  RR("RR", 1),
  RRA("RRA", 0),
  RRC("RRC", 1),
  RRCA("RRCQ", 0),
  RRD("RRD", 0),
  RST("RST", 1),
  SBC("SBC", 2),
  SCF("SCF", 0),
  SET("SET", 2, 3),
  SLA("SLA", 1),
  SLL("SLL", 1),
  SRA("SRA", 1),
  SRL("SRL", 1),
  SUB("SUB", 1),
  XOR("XOR", 1);

  private String text;

  private final int operandsLowerBound;

  private final int operandsUpperBound;

  Mnemonic(String text, int operandBounds) {
    this(text, operandBounds, operandBounds);
  }

  Mnemonic(String text, int operandsLowerBound, int operandsUpperBound) {
    this.text = text;
    this.operandsLowerBound = operandsLowerBound;
    this.operandsUpperBound = operandsUpperBound;
  }

  public int getOperandsLowerBound() {
    return operandsLowerBound;
  }

  public int getOperandsUpperBound() {
    return operandsUpperBound;
  }

  public static Mnemonic find(String text) {
    return Arrays.stream(values())
        .filter(mnemonic -> mnemonic.name().equalsIgnoreCase(text))
        .findAny()
        .orElse(null);
  }
}
