package dk.nikolajbrinch.faz80.parser.base;

public interface BaseMessage {

  MessageType type();

  String message();

  default boolean isError() {
    return type() == MessageType.ERROR;
  }
}
