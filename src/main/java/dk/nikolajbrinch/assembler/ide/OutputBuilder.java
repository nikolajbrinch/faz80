package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.Linked;

public class OutputBuilder {

  public String build(Linked linked) {
    StringBuilder builder = new StringBuilder();

    if (linked != null) {
      int address = linked.origin();
      builder.append(String.format("%04X: ", address & 0xFFFF));

      for (int i = 0; i < linked.linked().length; i++) {
        if (i > 0 && i % 16 == 0) {
          address += 16;
          builder.append(String.format("%n%04X: ", address & 0xFFFF));
        }

        builder.append(String.format("%02X ", linked.linked()[i]));
      }
    }

    return builder.toString();
  }
}
