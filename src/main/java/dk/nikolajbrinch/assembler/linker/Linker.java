package dk.nikolajbrinch.assembler.linker;

import dk.nikolajbrinch.assembler.compiler.Assembled;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Linker {

  private List<LinkError> errors = new ArrayList<>();

  public LinkResult link(Assembled assembled) {
    errors.clear();

    List<Integer> bytes = null;

    try {
      bytes =
          assembled.getLines().stream()
              .flatMapToLong(line -> Arrays.stream(line.byteSource().getBytes()))
              .mapToObj(value -> (int) (value & 0xFF))
              .toList();
    } catch (Exception e) {
      reportError(e);
    }

    if (bytes == null) {
      return null;
    }

    byte[] linked = new byte[bytes.size()];
    for (int i = 0; i < linked.length; i++) {
      linked[i] = bytes.get(i).byteValue();
    }

    return new LinkResult(new Linked((int) assembled.getOrigin(), linked), errors);
  }

  private void reportError(Exception e) {
    Throwable cause = e;

    if (cause instanceof InvocationTargetException invocationTargetException) {
      cause = invocationTargetException.getCause();
    }

    errors.add(new LinkError(new LinkException(cause.getMessage(), cause)));
  }
}
