package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.parser.base.BaseMessage;
import dk.nikolajbrinch.faz80.parser.base.MessageType;
import dk.nikolajbrinch.faz80.parser.statements.Statement;

public record AssembleMessage(MessageType type, String message, Statement statement)
    implements BaseMessage {

  public static AssembleMessage error(Statement statement, String message) {
    return new AssembleMessage(MessageType.ERROR, message, statement);
  }

  public static AssembleMessage error(AssembleException exception) {
    return error(exception.getStatement(), exception.getMessage());
  }

  public static AssembleMessage warning(Statement statement, String message) {
    return new AssembleMessage(MessageType.WARNING, message, statement);
  }
}
