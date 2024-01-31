package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.parser.Register;
import java.util.Map;

public class Registers {

  public static Map<Register, Integer> r =
      Map.of(
          Register.B,
          0b000,
          Register.C,
          0b001,
          Register.D,
          0b010,
          Register.E,
          0b011,
          Register.H,
          0b100,
          Register.L,
          0b101,
          Register.A,
          0b111);

  public static Map<Register, Integer> ss =
      Map.of(Register.BC, 0b00, Register.DE, 0b01, Register.HL, 0b10, Register.SP, 0b11);

  public static Map<Register, Integer> dd =
      Map.of(Register.BC, 0b00, Register.DE, 0b01, Register.HL, 0b10, Register.SP, 0b11);

  public static Map<Register, Integer> pp =
      Map.of(Register.BC, 0b00, Register.DE, 0b01, Register.IX, 0b10, Register.SP, 0b11);

  public static Map<Register, Integer> qq =
      Map.of(Register.BC, 0b00, Register.DE, 0b01, Register.HL, 0b10, Register.AF, 0b11);

  public static Map<Register, Integer> rr =
      Map.of(Register.BC, 0b00, Register.DE, 0b01, Register.IY, 0b10, Register.SP, 0b11);
}
