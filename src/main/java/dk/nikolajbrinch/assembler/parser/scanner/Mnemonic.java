package dk.nikolajbrinch.assembler.parser.scanner;

import java.util.Arrays;

public enum Mnemonic {
  ADC,
  ADD,
  AND,
  BIT,
  CALL,
  CCF,
  CP,
  CPD,
  CPDR,
  CPI,
  CPIR,
  CPL,
  DAA,
  DEC,
  DI,
  DJNZ,
  EI,
  EX,
  EXX,
  HALT,
  IM,
  IN,
  INC,
  IND,
  INDR,
  INI,
  INIR,
  JP,
  JR,
  LD,
  LDD,
  LDDR,
  LDI,
  LDIR,
  NEG,
  NOP,
  OR,
  OTDR,
  OTIR,
  OUT,
  OUTD,
  OUTI,
  POP,
  PUSH,
  RES,
  RET,
  RETI,
  RETN,
  RL,
  RLA,
  RLC,
  RLCA,
  RLD,
  RR,
  RRA,
  RRC,
  RRCA,
  RRD,
  RST,
  SBC,
  SCF,
  SET,
  SLA,
  SLL,
  SRA,
  SRL,
  SUB,
  XOR;

  public static Mnemonic find(String text) {
    return Arrays.stream(values())
        .filter(mnemonic -> mnemonic.name().equalsIgnoreCase(text))
        .findAny()
        .orElse(null);
  }
}