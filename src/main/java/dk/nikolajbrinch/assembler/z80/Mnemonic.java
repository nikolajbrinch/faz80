package dk.nikolajbrinch.assembler.z80;

import java.util.Arrays;

public enum Mnemonic {
  ADC(2),
  ADD(2),
  AND(1),
  BIT(2),
  CALL(1, 2),
  CCF(0),
  CP(1),
  CPD(0),
  CPDR(0),
  CPI(0),
  CPIR(0),
  CPL(0),
  DAA(0),
  DEC(1),
  DI(0),
  DJNZ(1),
  EI(0),
  EX(2),
  EXX(0),
  HALT(0),
  IM(1),
  IN(1, 2),
  INC(1),
  IND(0),
  INDR(0),
  INI(0),
  INIR(0),
  JP(1, 2),
  JR(1, 2),
  LD(2),
  LDD(0),
  LDDR(0),
  LDI(0),
  LDIR(0),
  NEG(0),
  NOP(0),
  OR(1),
  OTDR(0),
  OTIR(0),

  OUT(2),
  OUTD(0),
  OUTI(0),
  POP(1),
  PUSH(1),
  RES(2, 3),
  RET(0, 1),
  RETI(0),
  RETN(0),
  RL(1),
  RLA(0),
  RLC(1),
  RLCA(0),
  RLD(0),
  RR(1),
  RRA(0),
  RRC(1),
  RRCA(0),
  RRD(0),
  RST(1),
  SBC(2),
  SCF(0),
  SET(2, 3),
  SLA(1),
  SLL(1),
  SRA(1),
  SRL(1),
  SUB(1, 2),
  XOR(1);

  private final int operandsLowerBound;

  private final int operandsUpperBound;

  Mnemonic(int operandBounds) {
    this(operandBounds, operandBounds);
  }

  Mnemonic(int operandsLowerBound, int operandsUpperBound) {
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
