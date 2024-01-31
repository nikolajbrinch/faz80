package dk.nikolajbrinch.parser;

import java.util.List;

public interface TaskResult<T extends BaseError<?>> {

  List<T> errors();

  default boolean hasErrors() {
    return !errors().isEmpty();
  }
}
