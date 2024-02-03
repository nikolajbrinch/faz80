package dk.nikolajbrinch.faz80.assembler.operands;

public enum AddressingMode {
  UNKNOWN,

  IMMEDIATE,

  IMMEDIATE_EXTENDED,
  MODIFIED_PAGE_ZERO,
  RELATIVE,
  EXTENDED,

  INDEXED,
  REGISTER,
  IMPLIED,
  REGISTER_INDIRECT,
  BIT
}
