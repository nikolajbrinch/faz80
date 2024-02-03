package dk.nikolajbrinch.faz80.assembler.operands;

import dk.nikolajbrinch.faz80.parser.Condition;
import java.util.Map;

public class Conditions {

  public static Map<Condition, Integer> cc =
      Map.of(
          Condition.NZ,
          0b000,
          Condition.Z,
          0b001,
          Condition.NC,
          0b010,
          Condition.C,
          0b011,
          Condition.PO,
          0b100,
          Condition.PE,
          0b101,
          Condition.P,
          0b110,
          Condition.M,
          0b111);
}
