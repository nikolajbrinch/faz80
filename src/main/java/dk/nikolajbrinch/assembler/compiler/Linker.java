package dk.nikolajbrinch.assembler.compiler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Linker implements ErrorProducer<LinkException, LinkError> {

  private List<LinkError> errors = new ArrayList<>();

  public Linked link(Assembled assembled) {
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

    return new Linked((int) assembled.getOrigin(), linked);
  }

  private void reportError(Exception e) {
    Throwable cause = e;

    if (cause instanceof InvocationTargetException invocationTargetException) {
      cause = invocationTargetException.getCause();
    }

    errors.add(new LinkError(new LinkException(cause.getMessage(), cause)));
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public List<LinkError> getErrors() {
    return errors;
  }
}
