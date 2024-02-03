package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.parser.statements.Statement;
import dk.nikolajbrinch.faz80.base.errors.BaseException;

public class AssembleException extends BaseException {

  private final Statement statement;

  public AssembleException(Statement statement, String message) {
    super(message);
    this.statement = statement;
  }

  public AssembleException(Statement statement, String message, Throwable cause) {
    super(message, cause);
    this.statement = statement;
  }

  public Statement getStatement() {
    return statement;
  }
}
