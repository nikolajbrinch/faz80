package dk.nikolajbrinch.faz80.linker;


import dk.nikolajbrinch.faz80.parser.base.BaseMessage;
import dk.nikolajbrinch.faz80.parser.base.MessageType;

public record LinkMessage(MessageType type, String message) implements BaseMessage {

  public static LinkMessage error(String message) {
    return new LinkMessage(MessageType.ERROR, message);
  }

  public static LinkMessage warning(String message) {
    return new LinkMessage(MessageType.WARNING, message);
  }
}
