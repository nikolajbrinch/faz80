package dk.nikolajbrinch.assembler.compiler;

import java.util.Arrays;
import java.util.List;

public class Linker {

  public Linked link(Assembled assembled) {
    final List<Integer> bytes =
        assembled.getBytes().stream()
            .flatMapToLong(source -> Arrays.stream(source.getBytes()))
            .mapToObj(value -> (int) (value & 0xFF))
            .toList();

    byte[] linked = new byte[bytes.size()];
    for (int i = 0; i < linked.length; i++) {
      linked[i] = bytes.get(i).byteValue();
    }

    return new Linked((int) assembled.getOrigin(), linked);
  }
}
