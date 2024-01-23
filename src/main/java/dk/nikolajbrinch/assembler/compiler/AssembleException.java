package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.parser.BaseException;

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
