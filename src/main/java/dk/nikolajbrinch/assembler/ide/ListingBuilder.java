package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.Assembled;
import dk.nikolajbrinch.assembler.compiler.AssembledLine;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ListingBuilder {

  public String build(Assembled assembled) {
    StringBuilder builder = new StringBuilder();

    if (assembled != null) {
      int address = (int) assembled.getOrigin();

      int indent = 0;
      for (AssembledLine line : assembled.getLines()) {
        ByteSource bytes = line.byteSource();
        indent =
            Math.max(
                indent,
                Arrays.stream(bytes.getBytes())
                    .mapToObj(value -> String.format("%02X", value & 0xFF))
                    .collect(Collectors.joining(" "))
                    .length());
      }

      for (AssembledLine line : assembled.getLines()) {
        ByteSource bytes = line.byteSource();
        builder.append(String.format("%04X: ", address & 0xFFFF));
        String byteText =
            Arrays.stream(bytes.getBytes())
                .mapToObj(value -> String.format("%02X", value & 0xFF))
                .collect(Collectors.joining(" "));
        builder.append(byteText);
        builder.append(" ".repeat(indent - byteText.length() + 1));
        builder.append(
            String.format(
                "| %s\n", String.valueOf(line.statement().line().content().stripTrailing())));
        address += bytes.length();
      }
    }

    return builder.toString();
  }
}
