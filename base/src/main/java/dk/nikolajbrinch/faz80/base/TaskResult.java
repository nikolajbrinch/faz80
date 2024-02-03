package dk.nikolajbrinch.faz80.base;

import dk.nikolajbrinch.faz80.base.errors.BaseError;
import java.util.List;

public interface TaskResult<T extends BaseError<?>> {

  List<T> errors();

  default boolean hasErrors() {
    return !errors().isEmpty();
  }
}
