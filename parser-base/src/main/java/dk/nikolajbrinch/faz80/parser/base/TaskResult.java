package dk.nikolajbrinch.faz80.parser.base;

import java.util.List;

public interface TaskResult<T extends BaseMessage> {

  List<T> messages();

  default boolean hasErrors() {
    return messages().stream().anyMatch(BaseMessage::isError);
  }
}
