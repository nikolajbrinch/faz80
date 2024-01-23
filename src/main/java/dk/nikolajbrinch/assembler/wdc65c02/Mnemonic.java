package dk.nikolajbrinch.assembler.wdc65c02;

import java.util.Arrays;

public enum Mnemonic {
  ADC(2),
  AND(2),
  ASL(1),
  BBR(2),
  BBS(1, 2),
  BCC(0),
  BCS(1),
  BEQ(0),
  BIT(0),
  BMI(0),
  BNE(0),
  BPL(0),
  BRA(0),
  BRK(1),
  BVC(0),
  BVS(1),
  CLC(0),
  CLD(2),
  CLI(0),
  CLV(0),
  CMP(1),
  CPX(1, 2),
  CPY(1),
  DEC(0),
  DEX(0),
  DEY(0),
  EOR(0),
  INC(1, 2),
  INX(1, 2),
  INY(2),
  JMP(0),
  JSR(0),
  LDA(0),
  LDX(0),
  LDY(0),
  LSR(0),
  NOP(1),
  ORA(0),
  PHA(0),

  PHP(2),
  PHX(0),
  PHY(0),
  PLA(1),
  PLP(1),
  PLX(2, 3),
  PLY(0, 1),
  RMB(0),
  POL(0),
  ROR(1),
  RTI(0),
  RTS(1),
  SBC(0),
  SEC(0),
  SED(1),
  SEI(0),
  SMB(1),
  STA(0),
  STP(0),
  STX(1),
  STY(2),
  STZ(0),
  TAX(2, 3),
  TAY(1),
  TRB(1),
  TSB(1),
  TSX(1),
  TXA(1),
  TXS(1),
  TYA(1),
  WAI(1);

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
