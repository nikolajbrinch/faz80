package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.faz80.parser.base.BaseMessage;
import dk.nikolajbrinch.faz80.parser.base.MessageType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ParseMessage(MessageType type, String message, AssemblerToken token)
    implements BaseMessage {

  public static ParseMessage error(AssemblerToken token, String message) {
    return new ParseMessage(MessageType.ERROR, message, token);
  }

  public static ParseMessage error(ParseException exception) {
    return error(exception.getToken(), exception.getMessage());
  }

  public static ParseMessage warning(AssemblerToken token, String message) {
    return new ParseMessage(MessageType.WARNING, message, token);
  }
}
